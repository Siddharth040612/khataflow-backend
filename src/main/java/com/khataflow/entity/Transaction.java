package com.khataflow.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long storeId;
    private Long partyId;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    private Double amount;

    private String billNumber;

    private Long billId;

    private String description;

    private Boolean isDeleted = false;

    private LocalDateTime createdAt;
    private Long createdBy;

    private LocalDateTime updatedAt;
    private Long updatedBy;
}