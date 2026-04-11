package com.khataflow.service;

import com.khataflow.entity.Bill;
import com.khataflow.entity.Transaction;
import com.khataflow.repository.BillRepository;
import com.khataflow.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BillService {

    private final BillRepository billRepository;
    private final ValidationService validationService;
    private final SupabaseService supabaseService;
    private final TransactionRepository transactionRepository;

    public String upload(MultipartFile file,
                         Long storeId,
                         Long partyId,
                         Long transactionId,
                         String billNumber) {

        // 🔐 Validate
        validationService.validateParty(partyId, storeId);

        // 📁 File name
        String baseName;

        if (billNumber != null && !billNumber.isBlank()) {
            baseName = billNumber;
        } else {
            baseName = "bill";
        }

        String fileName = storeId + "/" + partyId + "/" +
                baseName + "_" + System.currentTimeMillis() + ".jpg";

        // ☁️ Upload
        String fileUrl = supabaseService.uploadFile(file, fileName);

        // 💾 Save DB
        Bill bill = new Bill();
        bill.setStoreId(storeId);
        bill.setPartyId(partyId);
        bill.setTransactionId(transactionId);
        bill.setFileUrl(fileUrl);
        bill.setBillNumber(billNumber);
        bill.setCreatedAt(LocalDateTime.now());

        Bill saved = billRepository.save(bill);

        // 🔗 Link transaction
        if (transactionId != null) {
            Transaction txn = transactionRepository
                    .findByIdAndStoreId(transactionId, storeId)
                    .orElseThrow(() -> new RuntimeException("Transaction not found"));

            txn.setBillId(saved.getId());
            transactionRepository.save(txn);
        }

        return fileUrl;
    }
}