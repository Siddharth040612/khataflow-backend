package com.khataflow.service;

import com.khataflow.dto.ShareRequest;
import com.khataflow.dto.ShareResponse;
import com.khataflow.entity.*;
import com.khataflow.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShareService {

    private final TransactionRepository transactionRepository;
    private final PartyRepository partyRepository;
    private final StoreRepository storeRepository;
    private final SettingsRepository settingsRepository;

    private static final DecimalFormat DF = new DecimalFormat("0.##");
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd MMM");

    public ShareResponse generate(ShareRequest request) {

        Party party = partyRepository.findById(request.getPartyId())
                .orElseThrow(() -> new RuntimeException("Party not found"));

        Store store = storeRepository.findById(request.getStoreId())
                .orElseThrow(() -> new RuntimeException("Store not found"));

        Settings settings = settingsRepository.findByStoreId(request.getStoreId())
                .orElse(null);

        String partyName = clean(party.getName());
        String storeName = clean(store.getName());

        boolean includeBills = request.getIncludeBills() != null
                ? request.getIncludeBills()
                : (settings != null && settings.getIncludeBillDefault());

        // 🔥 SINGLE DB CALL
        List<Object[]> results =
                transactionRepository.findTransactionsWithBills(
                        request.getStoreId(),
                        request.getPartyId()
                );

        double balance = 0;

        List<String> billLines = new ArrayList<>();
        int count = 1;

        for (Object[] row : results) {

            Transaction t = (Transaction) row[0];
            Bill b = (Bill) row[1];

            // 🔹 Balance
            if (t.getType() == TransactionType.CREDIT) {
                balance += t.getAmount();
            } else {
                balance -= t.getAmount();
            }

            // 🔹 Bills (limit 5)
            if (includeBills && b != null && billLines.size() < 5) {

                String billNumber = t.getBillNumber() != null
                        ? t.getBillNumber()
                        : "-";

                String amount = "₹" + DF.format(t.getAmount());

                String date = t.getCreatedAt() != null
                        ? t.getCreatedAt().toLocalDate().format(DATE_FORMAT)
                        : "-";

                String line =
                        "📄 Bill " + (count++) + "\n" +
                                "No: " + billNumber + " | " + amount + " | " + date + "\n" +
                                "View 👉 " + b.getFileUrl() + "\n";

                billLines.add(line);
            }
        }

        // 🔹 Build bills section
        String billsSection = "";
        if (includeBills) {
            if (billLines.isEmpty()) {
                billsSection = "📄 No recent bills available.\n";
            } else {
                billsSection = "📑 *Recent Bills*\n\n" +
                        String.join("\n", billLines);
            }
        }

        // 🔹 UPI LINK
        String upiLink = "upi://pay?" +
                "pa=" + store.getUpiId() +
                "&pn=" + encode(storeName) +
                "&am=" + DF.format(Math.max(balance, 0)) +
                "&cu=INR";

        String message;

        // 🔹 ZERO
        if (balance == 0) {

            message =
                    "Hi " + partyName + ",\n\n" +
                            "✅ Your account is settled.\n" +
                            "No pending dues.\n\n" +
                            "Thanks,\n" + storeName;

            return buildResponse(0.0, null, message, party.getPhone());
        }

        // 🔹 ADVANCE
        if (balance < 0) {

            double advance = Math.abs(balance);

            message =
                    "Hi " + partyName + ",\n\n" +
                            "💰 You have an advance balance of ₹" + DF.format(advance) + ".\n\n" +
                            "Thanks,\n" + storeName;

            return buildResponse(balance, null, message, party.getPhone());
        }

        // 🔥 MAIN MESSAGE
//        message =
//                "Hi " + partyName + ",\n\n" +
//
//                        "🧾 *Pending Balance*\n" +
//                        "₹" + DF.format(balance) + "\n\n" +
//
//                        "💳 *Pay Now*\n" +
//                        upiLink + "\n\n" +
//
//                        (includeBills ? billsSection + "\n" : "") +
//
//                        "🙏 Thank you!\n" +
//                        storeName;
        message =
                "Hi " + partyName + ",\n\n" +

                        "🧾 *Pending Balance*\n" +
                        "₹" + DF.format(balance) + "\n\n" +

                        "💳 *Pay Now*\n" +
                        upiLink + "\n\n" +

                        (includeBills ? billsSection + "\n" : "") +

                        "🙏 Thank you!\n" +
                        storeName;

        return buildResponse(balance, upiLink, message, party.getPhone());
    }

    // 🔹 RESPONSE BUILDER
    private ShareResponse buildResponse(double balance, String upiLink, String message, String phone) {

        message = clean(message);

        String encoded = URLEncoder.encode(message, StandardCharsets.UTF_8);

        String whatsappUrl = "https://wa.me/91" + phone.trim()
                + "?text=" + encoded;

        return new ShareResponse(balance, upiLink, message, whatsappUrl);
    }

    // 🔹 CLEAN STRING
    private String clean(String input) {
        if (input == null) return "";
        return input
                .replace("\u00A0", " ")     // fix weird spaces
                .replaceAll("[ \\t]+", " ") // only normal spaces
                .trim();
    }

    // 🔹 ENCODE
    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}