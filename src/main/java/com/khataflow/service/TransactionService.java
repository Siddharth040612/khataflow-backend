package com.khataflow.service;

import com.khataflow.dto.BalanceResponse;
import com.khataflow.dto.TransactionRequest;
import com.khataflow.dto.TransactionResponse;
import com.khataflow.entity.Transaction;
import com.khataflow.entity.TransactionType;
import com.khataflow.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

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
    public Page<TransactionResponse> getTransactions(
            Long storeId,
            Long partyId,
            int page,
            int size
    ) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        Page<Transaction> txnPage =
                repository.findByStoreIdAndPartyIdAndIsDeletedFalse(
                        storeId,
                        partyId,
                        pageable
                );

        List<Transaction> allTxns =
                repository.findByStoreIdAndPartyIdAndIsDeletedFalseOrderByCreatedAtDesc(
                        storeId,
                        partyId
                );

        List<TransactionResponse> allTxnsWithUrl =
                repository.getTransactionsWithFileurl(
                        storeId,
                        partyId
                );

        Map<Long, Double> balanceMap = new HashMap<>();

        double balance = 0;

        for (TransactionResponse t : allTxnsWithUrl) {
            if (t.getType() == TransactionType.CREDIT) {
                balance += t.getAmount();
            } else {
                balance -= t.getAmount();
            }
            balanceMap.put(t.getId(), balance);
        }

        for (TransactionResponse t : allTxnsWithUrl) {
            t.setRunningBalance(balanceMap.get(t.getId()));
        }

        // 🔹 Step 4: Map paginated result + inject balance
//        return txnPage.map(t -> {
//            TransactionResponse res = mapToResponse(t);
//            res.setRunningBalance(balanceMap.get(t.getId()));
//            return res;
//        });

//        return new PageImpl<>(
//                allTxnsWithUrl,
//                pageable,
//                allTxnsWithUrl.size()
//        );
        return txnPage.map(t -> {
            TransactionResponse res = mapToResponse(t);

            // 🔥 inject running balance
            Double runningBalance = balanceMap.get(t.getId());
            res.setRunningBalance(runningBalance);

            // 🔥 also set billUrl if needed
            Optional<TransactionResponse> full =
                    allTxnsWithUrl.stream()
                            .filter(x -> x.getId().equals(t.getId()))
                            .findFirst();

            full.ifPresent(f -> {
                res.setBillUrl(f.getBillUrl());
                res.setPartyName(f.getPartyName());
            });

            return res;
        });
    }

    public Page<TransactionResponse> getGroupedTransactions(
            Long storeId,
            Long partyId,
            String fromDate,
            String toDate,
            int page,
            int size
    ) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        // ✅ FILTER + PAGINATION IN DB
        Page<Transaction> txnPage;

        if (fromDate != null && toDate != null) {
            LocalDateTime from = LocalDate.parse(fromDate).atStartOfDay();
            LocalDateTime to = LocalDate.parse(toDate).atTime(LocalTime.MAX);

            txnPage = repository.findFilteredTransactionsPaged(
                    storeId,
                    partyId,
                    from,
                    to,
                    pageable
            );
        } else {
            txnPage = repository.findByStoreIdAndPartyIdAndIsDeletedFalse(
                    storeId,
                    partyId,
                    pageable
            );
        }

        // ✅ Fetch only needed IDs (NOT full table)
        List<Long> ids = txnPage.getContent().stream()
                .map(Transaction::getId)
                .toList();

        List<TransactionResponse> txnsWithUrl =
                repository.getTransactionsWithFileurl(storeId, partyId)
                        .stream()
                        .filter(t -> ids.contains(t.getId()))
                        .toList();

        // ✅ Running balance (still full list needed)
        List<TransactionResponse> fullList =
                repository.getTransactionsWithFileurl(storeId, partyId);

        Map<Long, Double> balanceMap = new HashMap<>();
        double balance = 0;

        for (TransactionResponse t : fullList) {
            if (t.getType() == TransactionType.CREDIT) {
                balance += t.getAmount();
            } else {
                balance -= t.getAmount();
            }
            balanceMap.put(t.getId(), balance);
        }

        return txnPage.map(t -> {
            TransactionResponse res = new TransactionResponse(
                    t.getId(),
                    t.getPartyId(),
                    null,
                    t.getType(),
                    t.getAmount(),
                    t.getBillNumber(),
                    null,
                    t.getDescription(),
                    t.getCreatedAt(),
                    t.getCreatedBy(),
                    null,
                    balanceMap.get(t.getId())
            );

            txnsWithUrl.stream()
                    .filter(x -> x.getId().equals(t.getId()))
                    .findFirst()
                    .ifPresent(x -> {
                        res.setBillUrl(x.getBillUrl());
                        res.setPartyName(x.getPartyName());
                    });

            return res;
        });
    }
    private TransactionResponse mapToResponse(Transaction t) {
        return new TransactionResponse(
                t.getId(),
                t.getPartyId(),
                null, // partyName if needed
                t.getType(),
                t.getAmount(),
                t.getBillNumber(),
                null, // billUrl (already handled elsewhere)
                t.getDescription(),
                t.getCreatedAt(),
                t.getCreatedBy(),
                null,
                null // runningBalance (we'll improve later)
        );
    }
}