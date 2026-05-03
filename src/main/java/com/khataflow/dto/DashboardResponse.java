package com.khataflow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {

    private double totalReceivable;
    private double totalPayable;
    private double netBalance;

    private List<RecentTransaction> recentTransactions;
    private List<RecentParty> recentParties;
}
