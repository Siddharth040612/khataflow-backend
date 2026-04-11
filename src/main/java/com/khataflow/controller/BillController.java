package com.khataflow.controller;

import com.khataflow.common.ApiResponse;
import com.khataflow.service.BillService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/bills")
@RequiredArgsConstructor
public class BillController {

    private final BillService billService;

    @PostMapping("/upload")
    public ApiResponse<String> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam Long storeId,
            @RequestParam Long partyId,
            @RequestParam(required = false) Long transactionId,
            @RequestParam(required = false) String billNumber
    ) {
        return ApiResponse.success(
                billService.upload(file, storeId, partyId, transactionId, billNumber)
        );
    }
}