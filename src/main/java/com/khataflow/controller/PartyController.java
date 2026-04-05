package com.khataflow.controller;

import com.khataflow.common.ApiResponse;
import com.khataflow.dto.PartyUpdateRequest;
import com.khataflow.entity.Party;
import com.khataflow.service.PartyService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/parties")
public class PartyController {

    private final PartyService service;

    public PartyController(PartyService service) {
        this.service = service;
    }

    // ✅ CREATE
    @PostMapping
    public ApiResponse<Party> create(@RequestBody Party party) {
        return ApiResponse.success(service.create(party));
    }

    // ✅ GET ALL
    @GetMapping
    public ApiResponse<List<Party>> getAll(@RequestParam Long storeId) {
        return ApiResponse.success(service.getAll(storeId));
    }

    // ✅ SEARCH
    @GetMapping("/search")
    public ApiResponse<List<Party>> search(
            @RequestParam Long storeId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String externalId,
            @RequestParam(required = false) Long id
    ) {
        return ApiResponse.success(service.search(storeId, name, phone, externalId, id));
    }

    // ✅ UPDATE
    @PatchMapping("/{id}")
    public ApiResponse<Party> update(
            @PathVariable Long id,
            @RequestParam Long storeId,
            @RequestBody PartyUpdateRequest request
    ) {
        return ApiResponse.success(service.update(id, storeId, request));
    }

    // ✅ DELETE
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(
            @PathVariable Long id,
            @RequestParam Long storeId
    ) {
        service.delete(id, storeId);
        return ApiResponse.successMessage("Deleted successfully");
    }
}