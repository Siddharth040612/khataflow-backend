package com.khataflow.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "bills")
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long storeId;
    private Long partyId;
    private Long transactionId;

    private String fileUrl;
    private String billNumber;

    private LocalDateTime createdAt;
}