package com.khataflow.controller;

import com.khataflow.common.ApiResponse;
import com.khataflow.dto.SettingsRequest;
import com.khataflow.entity.Settings;
import com.khataflow.service.SettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/settings")
@RequiredArgsConstructor
public class SettingsController {

    private final SettingsService service;

    @PostMapping
    public ApiResponse<Settings> save(@RequestBody SettingsRequest request) {
        return ApiResponse.success(service.save(request));
    }

    @GetMapping
    public ApiResponse<Settings> get(@RequestParam Long storeId) {
        return ApiResponse.success(service.get(storeId));
    }
}