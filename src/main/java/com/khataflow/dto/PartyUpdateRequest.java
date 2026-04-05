package com.khataflow.dto;

import com.khataflow.entity.PartyType;
import lombok.Data;

@Data
public class PartyUpdateRequest {

    private String name;
    private String phone;
    private String email;
    private PartyType partyType;
    private String externalId;
}