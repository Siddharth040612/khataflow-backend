package com.khataflow.controller;

import com.khataflow.common.ApiResponse;
import com.khataflow.dto.ShareRequest;
import com.khataflow.dto.ShareResponse;
import com.khataflow.service.ShareService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/share")
@RequiredArgsConstructor
public class ShareController {

    private final ShareService service;

    @PostMapping
    public ApiResponse<ShareResponse> generate(@RequestBody ShareRequest request) {
        return ApiResponse.success(service.generate(request));
    }
}