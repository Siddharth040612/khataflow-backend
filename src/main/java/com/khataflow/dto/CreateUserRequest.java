package com.khataflow.dto;

import com.khataflow.entity.UserRole;
import lombok.Data;

@Data
public class CreateUserRequest {

    private Long storeId;

    private String name;
    private String phone;
    private String email;

    private UserRole role; // ADMIN / STAFF
}