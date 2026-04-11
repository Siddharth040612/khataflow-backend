package com.khataflow.dto;

import lombok.Data;

@Data
public class SettingsRequest {

    private Long storeId;
    private String whatsappTemplate;
    private Boolean includeBillDefault;
}