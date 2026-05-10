package com.khataflow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecentPartyResponse {

    private Long id;
    private String name;
    private Double balance;
    private String phone;
    private LocalDateTime lastTransactionAt;
}
