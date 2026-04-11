package com.khataflow.dto;

import lombok.Data;

@Data
public class ShareRequest {

    private Long storeId;
    private Long partyId;

    private Boolean includeBills; // optional override
}