package com.khataflow.controller;

import com.khataflow.common.ApiResponse;
import com.khataflow.dto.BalanceResponse;
import com.khataflow.dto.TransactionRequest;
import com.khataflow.dto.TransactionResponse;
import com.khataflow.entity.Transaction;
import com.khataflow.service.TransactionService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService service;

    public TransactionController(TransactionService service) {
        this.service = service;
    }


    @PostMapping
    public ApiResponse<Transaction> create(@RequestBody TransactionRequest request) {
        return ApiResponse.success(service.create(request));
    }


//    @GetMapping
//    public ApiResponse<List<Transaction>> getTransactions(
//            @RequestParam Long storeId,
//            @RequestParam Long partyId,
//            @RequestParam(required = false) String type,
//            @RequestParam(required = false) String billNumber,
//            @RequestParam(required = false) String fromDate,
//            @RequestParam(required = false) String toDate
//    ) {
//        return ApiResponse.success(
//                service.getTransactions(storeId, partyId, type, billNumber, fromDate, toDate)
//        );
//    }

    @GetMapping
    public ApiResponse<List<TransactionResponse>> getTransactions(
            @RequestParam Long storeId,
            @RequestParam Long partyId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String billNumber,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate
    ) {
        return ApiResponse.success(
                service.getTransactions(storeId, partyId, type, billNumber, fromDate, toDate)
        );
    }


    @GetMapping("/balance")
    public ApiResponse<BalanceResponse> getBalance(
            @RequestParam Long storeId,
            @RequestParam Long partyId
    ) {
        return ApiResponse.success(service.getBalance(storeId, partyId));
    }


    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(
            @PathVariable Long id,
            @RequestParam Long storeId
    ) {
        service.delete(id, storeId);
        return ApiResponse.successMessage("Transaction deleted successfully");
    }

    @GetMapping("/grouped")
    public ApiResponse<Page<TransactionResponse>> getTransactions(
            @RequestParam Long storeId,
            @RequestParam Long partyId,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        return new ApiResponse<>(
                true,
                service.getGroupedTransactions(storeId, partyId, fromDate, toDate, page, size),
                null,
                null
        );
    }
}