package com.khataflow.service;

import com.khataflow.dto.BalanceResponse;
import com.khataflow.dto.TransactionRequest;
import com.khataflow.dto.TransactionResponse;
import com.khataflow.entity.Transaction;
import com.khataflow.entity.TransactionType;
import com.khataflow.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository repository;

    private final ValidationService validationService;

    public TransactionService(TransactionRepository repository,
                              ValidationService validationService) {
        this.repository = repository;
        this.validationService = validationService;
    }

    public Transaction create(TransactionRequest request) {

        if (request.getAmount() == null || request.getAmount() <= 0) {
            throw new RuntimeException("Amount must be greater than 0");
        }

        // 🔥 VALIDATION (IMPORTANT)
        validationService.validateParty(
                request.getPartyId(),
                request.getStoreId()
        );

        Transaction txn = new Transaction();
        txn.setStoreId(request.getStoreId());
        txn.setPartyId(request.getPartyId());
        txn.setType(request.getType());
        txn.setAmount(request.getAmount());
        txn.setBillNumber(request.getBillNumber());
        txn.setDescription(request.getDescription());
        txn.setCreatedAt(LocalDateTime.now());
        txn.setCreatedBy(1L);
        txn.setIsDeleted(false);

        return repository.save(txn);
    }


//    public List<TransactionResponse> getTransactions(
//            Long storeId,
//            Long partyId,
//            String type,
//            String billNumber,
//            String fromDate,
//            String toDate
//    ) {
//
//        validationService.validateParty(partyId, storeId);
//
//        List<TransactionResponse> list = repository.getTransactions(storeId, partyId);
//
//        // 🔍 Type filter
//        if (type != null) {
//            list = list.stream()
//                    .filter(t -> t.getType().name().equalsIgnoreCase(type))
//                    .toList();
//        }
//
//        // 🔍 Bill filter
//        if (billNumber != null) {
//            list = list.stream()
//                    .filter(t -> t.getBillNumber() != null &&
//                            t.getBillNumber().toLowerCase().contains(billNumber.toLowerCase()))
//                    .toList();
//        }
//
//        // 📅 Date filter
//        if (fromDate != null && toDate != null) {
//            java.time.LocalDate from = java.time.LocalDate.parse(fromDate);
//            java.time.LocalDate to = java.time.LocalDate.parse(toDate);
//
//            list = list.stream()
//                    .filter(t -> {
//                        if (t.getCreatedAt() == null) return false;
//                        java.time.LocalDate txnDate = t.getCreatedAt().toLocalDate();
//                        return !txnDate.isBefore(from) && !txnDate.isAfter(to);
//                    })
//                    .toList();
//        }
//
//        return list;
//    }

    public List<TransactionResponse> getTransactions(
            Long storeId,
            Long partyId,
            String type,
            String billNumber,
            String fromDate,
            String toDate
    ) {

        validationService.validateParty(partyId, storeId);

        List<TransactionResponse> list =
                repository.getTransactionsWithFileurl(storeId, partyId);
//        List<Transaction> list =
//                repository.findByStoreIdAndPartyIdAndIsDeletedFalseOrderByCreatedAtDesc(storeId, partyId);

        // 🔍 TYPE FILTER
        if (type != null && !type.isEmpty()) {
            TransactionType txnType = TransactionType.valueOf(type.toUpperCase());
            list = list.stream()
                    .filter(t -> t.getType() == txnType)
                    .toList();
        }

        // 🔍 BILL FILTER
        if (billNumber != null && !billNumber.isEmpty()) {
            list = list.stream()
                    .filter(t -> t.getBillNumber() != null &&
                            t.getBillNumber().toLowerCase().contains(billNumber.toLowerCase()))
                    .toList();
        }

        // 📅 DATE FILTER
        if (fromDate != null && toDate != null) {
            LocalDate from = LocalDate.parse(fromDate);
            LocalDate to = LocalDate.parse(toDate);

            list = list.stream()
                    .filter(t -> {
                        if (t.getCreatedAt() == null) return false;
                        LocalDate txnDate = t.getCreatedAt().toLocalDate();
                        return !txnDate.isBefore(from) && !txnDate.isAfter(to);
                    })
                    .toList();
        }

        return applyRunningBalance(list);
    }

    private List<TransactionResponse> mapWithRunningBalance(List<Transaction> list) {

        // Step 1: reverse to ASC
        List<Transaction> ascList = new ArrayList<>(list);
        Collections.reverse(ascList);

        double balance = 0;
        List<TransactionResponse> temp = new ArrayList<>();

        // Step 2: calculate balance
        for (Transaction t : ascList) {

            if (t.getType() == TransactionType.CREDIT) {
                balance += t.getAmount();
            } else {
                balance -= t.getAmount();
            }

            TransactionResponse res = new TransactionResponse();
            res.setId(t.getId());
            res.setAmount(t.getAmount());
            res.setType(t.getType());
            res.setBillNumber(t.getBillNumber());
            res.setCreatedAt(t.getCreatedAt());
            res.setRunningBalance(balance);

            temp.add(res);
        }

        // Step 3: reverse back to DESC
        Collections.reverse(temp);

        return temp;
    }

    private List<TransactionResponse> applyRunningBalance(List<TransactionResponse> list) {

        List<TransactionResponse> asc = new ArrayList<>(list);
        Collections.reverse(asc);

        double balance = 0;

        for (TransactionResponse t : asc) {
            if (t.getType() == TransactionType.CREDIT) {
                balance += t.getAmount();
            } else {
                balance -= t.getAmount();
            }
            t.setRunningBalance(balance);
        }

        Collections.reverse(asc);
        return asc;
    }

//

    public BalanceResponse getBalance(Long storeId, Long partyId) {

        validationService.validateParty(partyId, storeId);
        Object[] result = repository.getBalance(storeId, partyId);

        Double credit = 0.0;
        Double payment = 0.0;

        if (result != null && result.length > 0) {

            Object[] row = (Object[]) result[0]; // 🔥 FIX

            if (row != null && row.length >= 2) {
                credit = row[0] != null ? ((Number) row[0]).doubleValue() : 0.0;
                payment = row[1] != null ? ((Number) row[1]).doubleValue() : 0.0;
            }
        }

        return new BalanceResponse(
                credit,
                payment,
                credit - payment
        );
    }


    public void delete(Long id, Long storeId) {

        Transaction txn = repository.findByIdAndStoreId(id, storeId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        txn.setIsDeleted(true);
        txn.setUpdatedAt(LocalDateTime.now());
        txn.setUpdatedBy(1L);

        repository.save(txn);
    }

}