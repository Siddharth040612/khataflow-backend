package com.khataflow.dto;

import lombok.Data;

@Data
public class CreateStoreRequest {

    private String storeName;
    private String storePhone;
    private String upiId;

    private String adminName;
    private String adminPhone;
    private String adminEmail;
}