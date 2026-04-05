package com.khataflow.common;

import lombok.Data;

@Data
public class ApiResponse<T> {

    private boolean success;
    private T data;
    private String message;
    private String error;

    // Success with data
    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> res = new ApiResponse<>();
        res.success = true;
        res.data = data;
        return res;
    }

    // Success with message
    public static <T> ApiResponse<T> successMessage(String message) {
        ApiResponse<T> res = new ApiResponse<>();
        res.success = true;
        res.message = message;
        return res;
    }

    // Error
    public static <T> ApiResponse<T> error(String error) {
        ApiResponse<T> res = new ApiResponse<>();
        res.success = false;
        res.error = error;
        return res;
    }
}