package com.khataflow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryResponse {

    private double totalReceivable;
    private double totalPayable;
    private double netBalance;
    private long totalParties;
    private long activeParties;
}
