package com.khataflow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BalanceResponse {

    private Double totalCredit;
    private Double totalPayment;
    private Double balance;
}