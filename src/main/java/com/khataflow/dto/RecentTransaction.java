package com.khataflow.dto;

import com.khataflow.entity.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecentTransaction {

    private Long id;
    private String partyName;
    private Double amount;
    private TransactionType type;
    private LocalDateTime createdAt;
}
