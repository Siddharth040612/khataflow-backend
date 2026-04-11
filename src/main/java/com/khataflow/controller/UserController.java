package com.khataflow.controller;

import com.khataflow.common.ApiResponse;
import com.khataflow.dto.CreateUserRequest;
import com.khataflow.entity.User;
import com.khataflow.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    // 🔥 CREATE USER
    @PostMapping
    public ApiResponse<User> create(@RequestBody CreateUserRequest request) {
        return ApiResponse.success(service.create(request));
    }

    // 🔥 GET USERS
    @GetMapping
    public ApiResponse<List<User>> getUsers(@RequestParam Long storeId) {
        return ApiResponse.success(service.getUsers(storeId));
    }

    // 🔥 GET USER BY ID
    @GetMapping("/{id}")
    public ApiResponse<User> getById(
            @PathVariable Long id,
            @RequestParam Long storeId
    ) {
        return ApiResponse.success(service.getById(id, storeId));
    }

    // 🔥 DELETE USER
    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(
            @PathVariable Long id,
            @RequestParam Long storeId
    ) {
        service.delete(id, storeId);
        return ApiResponse.success("User deleted successfully");
    }
}