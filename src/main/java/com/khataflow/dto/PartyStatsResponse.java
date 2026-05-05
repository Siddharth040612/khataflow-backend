package com.khataflow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PartyStatsResponse {

    private double totalCredit;
    private double totalPayment;
    private long transactionCount;
    private LocalDateTime lastTransactionDate;
}
