package com.khataflow.controller;

import com.khataflow.common.ApiResponse;
import com.khataflow.dto.DashboardResponse;
import com.khataflow.dto.DashboardSummaryResponse;
import com.khataflow.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public ApiResponse<DashboardResponse> getDashboard(@RequestParam Long storeId) {
        DashboardResponse data = dashboardService.getDashboard(storeId);
        return ApiResponse.success(data);
    }

    @GetMapping("/summary")
    public ApiResponse<DashboardSummaryResponse> getDashboardSummary(@RequestParam Long storeId) {
        DashboardSummaryResponse data = dashboardService.getDashboardSummary(storeId);
        return ApiResponse.success(data);
    }
}
