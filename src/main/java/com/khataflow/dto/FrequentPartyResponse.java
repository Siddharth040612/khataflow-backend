package com.khataflow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FrequentPartyResponse {

    private Long id;
    private String name;
    private Double balance;
    private Long transactionCount;
}
