package com.khataflow.service;

import com.khataflow.dto.FrequentPartyResponse;
import com.khataflow.dto.PartyStatsResponse;
import com.khataflow.dto.RecentPartyResponse;
import com.khataflow.entity.Party;
import com.khataflow.repository.PartyRepository;
import com.khataflow.repository.TransactionRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PartyInsightService {

    private final PartyRepository partyRepository;
    private final TransactionRepository transactionRepository;

    public PartyInsightService(PartyRepository partyRepository, TransactionRepository transactionRepository) {
        this.partyRepository = partyRepository;
        this.transactionRepository = transactionRepository;
    }

    public List<RecentPartyResponse> getRecentParties(Long storeId) {
        Pageable pageable = PageRequest.of(0, 5);
        List<Party> parties = partyRepository.findPartiesByLastTransactionDate(storeId, pageable);

        return parties.stream()
                .map(party -> {
                    LocalDateTime lastTransactionDate = transactionRepository.getLastTransactionDate(storeId, party.getId());
                    Object[] balanceResult = transactionRepository.getBalance(storeId, party.getId());

                    double credit = 0.0;
                    double payment = 0.0;

                    Object[] row = balanceResult;
                    if (balanceResult != null && balanceResult.length > 0 && balanceResult[0] instanceof Object[]) {
                        row = (Object[]) balanceResult[0];
                    }

                    if (row != null && row.length >= 2) {
                        credit = row[0] != null ? ((Number) row[0]).doubleValue() : 0.0;
                        payment = row[1] != null ? ((Number) row[1]).doubleValue() : 0.0;
                    }

                    double balance = credit - payment;

                    return new RecentPartyResponse(
                            party.getId(),
                            party.getName(),
                            balance,
                            party.getPhone(),
                            lastTransactionDate
                    );
                })
                .collect(Collectors.toList());
    }

    public List<FrequentPartyResponse> getFrequentParties(Long storeId) {
        List<Object[]> frequentPartiesData = transactionRepository.findFrequentParties(storeId);

        return frequentPartiesData.stream()
                .map(row -> {
                    Long partyId = ((Number) row[0]).longValue();
                    String partyName = (String) row[1];
                    String partyPhone = (String) row[3];
                    Long transactionCount = ((Number) row[2]).longValue();

                    // Get party balance
                    Object[] balanceResult = transactionRepository.getBalance(storeId, partyId);
                    double credit = 0.0;
                    double payment = 0.0;

                    Object[] r = balanceResult;


                    if (balanceResult != null && balanceResult.length > 0 && balanceResult[0] instanceof Object[]) {
                        r = (Object[]) balanceResult[0];
                    }

                    if (r != null && r.length >= 2) {
                        credit = r[0] != null ? ((Number) r[0]).doubleValue() : 0.0;
                        payment = r[1] != null ? ((Number) r[1]).doubleValue() : 0.0;
                    }

                    double balance = credit - payment;

                    return new FrequentPartyResponse(
                            partyId,
                            partyName,
                            balance,
                            partyPhone,
                            transactionCount
                    );
                })
                .collect(Collectors.toList());
    }

    public PartyStatsResponse getPartyStats(Long storeId, Long partyId) {

        Object raw = transactionRepository.getPartyStats(storeId, partyId);

        Object[] row;

        // 🔥 HANDLE BOTH CASES SAFELY
        if (raw instanceof Object[]) {
            // case: correct single row
            row = (Object[]) raw;

            // BUT check if nested
            if (row.length > 0 && row[0] instanceof Object[]) {
                row = (Object[]) row[0]; // unwrap
            }

        } else {
            // fallback (should not happen)
            row = new Object[]{0, 0, 0};
        }

        double totalCredit = row[0] != null ? ((Number) row[0]).doubleValue() : 0.0;
        double totalPayment = row[1] != null ? ((Number) row[1]).doubleValue() : 0.0;
        long transactionCount = row[2] != null ? ((Number) row[2]).longValue() : 0;

        LocalDateTime lastTransactionDate =
                transactionRepository.getLastTransactionDate(storeId, partyId);

        return new PartyStatsResponse(
                totalCredit,
                totalPayment,
                transactionCount,
                lastTransactionDate
        );
    }
}
