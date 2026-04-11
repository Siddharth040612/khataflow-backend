package com.khataflow.dto;

import com.khataflow.entity.TransactionType;
import lombok.Data;

@Data
public class TransactionRequest {

    private Long storeId;
    private Long partyId;
    private TransactionType type;
    private Double amount;
    private String billNumber;
    private String description;
}