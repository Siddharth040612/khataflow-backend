package com.khataflow.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔹 Multi-tenant
    private Long storeId;

    // 🔹 Basic Info
    private String name;
    private String phone;
    private String email;

    // 🔹 Auth (future ready)
    private String password;

    // 🔹 Role (ADMIN / STAFF)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    // 🔹 Status (ACTIVE / INACTIVE / BLOCKED)
    @Enumerated(EnumType.STRING)
    private UserStatus status;

    // 🔹 Soft delete (optional future)
    private Boolean isDeleted = false;

    // 🔹 Audit
    private LocalDateTime createdAt;
    private Long createdBy;

    private LocalDateTime updatedAt;
    private Long updatedBy;
}