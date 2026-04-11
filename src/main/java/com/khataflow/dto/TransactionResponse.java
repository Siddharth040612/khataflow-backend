package com.khataflow.dto;

import com.khataflow.entity.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class TransactionResponse {

    private Long id;

    private Long partyId;
    private String partyName;

    private TransactionType type;
    private Double amount;

    private String billNumber;
    private String billUrl;
    private String description;

    private LocalDateTime createdAt;

    private Long createdBy;
    private String createdByName;

    private Double runningBalance;

    public TransactionResponse(
            Long id,
            Long partyId,
            String partyName,
            TransactionType type,
            Double amount,
            String billNumber,
            String description,
            LocalDateTime createdAt,
            Long createdBy,
            String createdByName,
            String billUrl
    ) {
        this.id = id;
        this.partyId = partyId;
        this.partyName = partyName;
        this.type = type;
        this.amount = amount;
        this.billNumber = billNumber;
        this.description = description;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.createdByName = createdByName;
        this.billUrl = billUrl;
    }
}