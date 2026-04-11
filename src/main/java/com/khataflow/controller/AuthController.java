package com.khataflow.controller;

import com.khataflow.common.ApiResponse;
import com.khataflow.entity.User;
import com.khataflow.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    public ApiResponse<User> login(@RequestParam String phone) {
        return ApiResponse.success(userService.getByPhone(phone));
    }
}