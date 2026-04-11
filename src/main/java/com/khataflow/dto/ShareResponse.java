package com.khataflow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ShareResponse {

    private Double balance;
    private String upiLink;
    private String message;
    private String whatsappUrl;
}