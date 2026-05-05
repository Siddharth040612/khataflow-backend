package com.khataflow.controller;

import com.khataflow.common.ApiResponse;
import com.khataflow.dto.FrequentPartyResponse;
import com.khataflow.dto.PartyStatsResponse;
import com.khataflow.dto.RecentPartyResponse;
import com.khataflow.service.PartyInsightService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/parties")
public class PartyInsightsController {

    private final PartyInsightService partyInsightService;

    public PartyInsightsController(PartyInsightService partyInsightService) {
        this.partyInsightService = partyInsightService;
    }

    @GetMapping("/recent")
    public ApiResponse<List<RecentPartyResponse>> getRecentParties(@RequestParam Long storeId) {
        List<RecentPartyResponse> data = partyInsightService.getRecentParties(storeId);
        return ApiResponse.success(data);
    }

    @GetMapping("/frequent")
    public ApiResponse<List<FrequentPartyResponse>> getFrequentParties(@RequestParam Long storeId) {
        List<FrequentPartyResponse> data = partyInsightService.getFrequentParties(storeId);
        return ApiResponse.success(data);
    }

    @GetMapping("/{id}/stats")
    public ApiResponse<PartyStatsResponse> getPartyStats(
            @PathVariable Long id,
            @RequestParam Long storeId) {
        PartyStatsResponse data = partyInsightService.getPartyStats(storeId, id);
        return ApiResponse.success(data);
    }
}
