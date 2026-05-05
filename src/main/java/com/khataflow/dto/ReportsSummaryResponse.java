package com.khataflow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportsSummaryResponse {

    private double totalCredit;
    private double totalPayment;
    private double netBalance;
    private long transactionCount;
}
