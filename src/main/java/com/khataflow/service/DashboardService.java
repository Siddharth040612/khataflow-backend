package com.khataflow.service;

import com.khataflow.dto.DashboardResponse;
import com.khataflow.dto.RecentParty;
import com.khataflow.dto.RecentTransaction;
import com.khataflow.entity.Party;
import com.khataflow.repository.PartyRepository;
import com.khataflow.repository.TransactionRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final TransactionRepository transactionRepository;
    private final PartyRepository partyRepository;

    public DashboardService(TransactionRepository transactionRepository, PartyRepository partyRepository) {
        this.transactionRepository = transactionRepository;
        this.partyRepository = partyRepository;
    }

    public DashboardResponse getDashboard(Long storeId) {
        double totalReceivable = transactionRepository.getTotalReceivable(storeId);
        double totalPayable = transactionRepository.getTotalPayable(storeId);
        double netBalance = totalReceivable - totalPayable;

        Pageable pageable5 = PageRequest.of(0, 5);
        List<RecentTransaction> recentTransactions = transactionRepository.findRecentTransactions(storeId, pageable5);

        List<Party> recentPartiesEntities = partyRepository.findRecentParties(storeId, pageable5);
        List<RecentParty> recentParties = recentPartiesEntities.stream()
                .map(party -> {
                    Object[] result = transactionRepository.getBalance(storeId, party.getId());
                    double credit = 0.0;
                    double payment = 0.0;
                    if (result != null && result.length >= 2) {
                        credit = result[0] != null ? ((Number) result[0]).doubleValue() : 0.0;
                        payment = result[1] != null ? ((Number) result[1]).doubleValue() : 0.0;
                    }
                    double balance = credit - payment;
                    return new RecentParty(party.getId(), party.getName(), balance);
                })
                .collect(Collectors.toList());

        return new DashboardResponse(totalReceivable, totalPayable, netBalance, recentTransactions, recentParties);
    }
}
