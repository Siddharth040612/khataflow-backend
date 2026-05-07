package com.khataflow.controller;

import com.khataflow.common.ApiResponse;
import com.khataflow.dto.CreateStoreRequest;
import com.khataflow.dto.StoreResponse;
import com.khataflow.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService service;

    @PostMapping
    public ApiResponse<Long> create(@RequestBody CreateStoreRequest request) {
        return ApiResponse.success(service.createStore(request));
    }

    @GetMapping("/{storeId}")
    public ApiResponse<StoreResponse> getStoreDetails(@PathVariable Long storeId) {
        StoreResponse storeResponse = service.getStoreDetails(storeId);
        return ApiResponse.success(storeResponse);
    }
}