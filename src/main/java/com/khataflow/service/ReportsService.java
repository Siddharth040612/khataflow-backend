package com.khataflow.service;

import com.khataflow.dto.ReportsSummaryResponse;
import com.khataflow.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class ReportsService {

    private final TransactionRepository transactionRepository;

    public ReportsService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public ReportsSummaryResponse getTransactionSummary(Long storeId, LocalDate startDate, LocalDate endDate) {
        // Convert dates to LocalDateTime (start of day to end of day)
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        Object[] result = transactionRepository.getTransactionSummaryByDateRange(storeId, startDateTime, endDateTime);

        double totalCredit = 0.0;
        double totalPayment = 0.0;
        long transactionCount = 0;

        if (result != null && result.length >= 3) {
            totalCredit = result[0] != null ? ((Number) result[0]).doubleValue() : 0.0;
            totalPayment = result[1] != null ? ((Number) result[1]).doubleValue() : 0.0;
            transactionCount = result[2] != null ? ((Number) result[2]).longValue() : 0;
        }

        double netBalance = totalCredit - totalPayment;

        return new ReportsSummaryResponse(totalCredit, totalPayment, netBalance, transactionCount);
    }
}
