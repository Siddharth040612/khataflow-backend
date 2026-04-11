package com.khataflow.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "settings")
@Data
public class Settings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long storeId;

    @Column(columnDefinition = "TEXT")
    private String whatsappTemplate;

    private Boolean includeBillDefault = true;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}