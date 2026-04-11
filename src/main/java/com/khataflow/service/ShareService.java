package com.khataflow.service;

import com.khataflow.dto.ShareRequest;
import com.khataflow.dto.ShareResponse;
import com.khataflow.entity.*;
import com.khataflow.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShareService {

    private final TransactionRepository transactionRepository;
    private final BillRepository billRepository;
    private final PartyRepository partyRepository;
    private final StoreRepository storeRepository;
    private final SettingsRepository settingsRepository;

    public ShareResponse generate(ShareRequest request) {

        // 🔹 Fetch data
        Party party = partyRepository.findById(request.getPartyId())
                .orElseThrow(() -> new RuntimeException("Party not found"));

        Store store = storeRepository.findById(request.getStoreId())
                .orElseThrow(() -> new RuntimeException("Store not found"));

        Settings settings = settingsRepository.findByStoreId(request.getStoreId())
                .orElse(null);

        // 🔹 Calculate balance
        List<Transaction> transactions =
                transactionRepository.findByStoreIdAndPartyIdAndIsDeletedFalse(
                        request.getStoreId(), request.getPartyId()
                );

        double balance = 0;

        for (Transaction t : transactions) {
            if (t.getType() == TransactionType.CREDIT) {
                balance += t.getAmount();
            } else {
                balance -= t.getAmount();
            }
        }

        if (balance == 0) {

            String message = "Hi " + party.getName() + ",\n\n" +
                    "You have no pending dues. Your account is settled.\n\n" +
                    "Thanks,\n" + store.getName();

            String encoded = URLEncoder.encode(message, StandardCharsets.UTF_8);

            String whatsappUrl = "https://wa.me/91" + party.getPhone() + "?text=" + encoded;

            return new ShareResponse(
                    0.0,
                    null,        // ❌ no UPI link
                    message,
                    whatsappUrl
            );
        }

        if (balance < 0) {

            double advanceAmount = Math.abs(balance); // 🔥 remove negative sign

            String message = "Hi " + party.getName() + ",\n\n" +
                    "You have an advance balance of ₹" + advanceAmount + ".\n\n" +
                    "Thanks,\n" + store.getName();

            String encoded = URLEncoder.encode(message, StandardCharsets.UTF_8);

            String whatsappUrl = "https://wa.me/91" + party.getPhone() + "?text=" + encoded;

            return new ShareResponse(
                    balance,   // keep actual value internally
                    null,
                    message,
                    whatsappUrl
            );
        }

        // 🔹 UPI link
        String upiLink = "upi://pay?" +
                "pa=" + store.getUpiId() +
                "&pn=" + store.getName() +
                "&am=" + balance +
                "&cu=INR";

        // 🔹 Bills
        boolean includeBills = request.getIncludeBills() != null
                ? request.getIncludeBills()
                : (settings != null && settings.getIncludeBillDefault());

        StringBuilder billsText = new StringBuilder();

        if (includeBills) {
            List<Bill> bills = billRepository
                    .findTop5ByStoreIdAndPartyIdOrderByCreatedAtDesc(
                            request.getStoreId(),
                            request.getPartyId()
                    );

            for (Bill b : bills) {
                billsText.append(b.getFileUrl()).append("\n");
            }
        }

        // 🔹 Template
        String template = (settings != null && settings.getWhatsappTemplate() != null)
                ? settings.getWhatsappTemplate()
                : defaultTemplate();

        String message = template
                .replace("{name}", party.getName())
                .replace("{amount}", String.valueOf(balance))
                .replace("{upiLink}", upiLink)
                .replace("{bills}", billsText.toString())
                .replace("{storeName}", store.getName());

        // 🔹 WhatsApp URL
        String encoded = URLEncoder.encode(message, StandardCharsets.UTF_8);

        String whatsappUrl = "https://wa.me/91" + party.getPhone() + "?text=" + encoded;

        return new ShareResponse(balance, upiLink, message, whatsappUrl);
    }

    private String defaultTemplate() {
        return "Hi {name},\n\n" +
                "Your pending balance is ₹{amount}.\n\n" +
                "Pay here:\n{upiLink}\n\n" +
                "{bills}\n\n" +
                "Thanks,\n{storeName}";
    }
}