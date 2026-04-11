package com.khataflow.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
public class SupabaseService {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    @Value("${supabase.bucket}")
    private String bucket;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ImageService imageService;

    public SupabaseService(ImageService imageService) {
        this.imageService = imageService;
    }

    public String uploadFile(MultipartFile file, String fileName) {

        // 🔒 Validate file exists
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File is required");
        }

// 🔒 Validate type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image")) {
            throw new RuntimeException("Only image files are allowed");
        }

// 🔒 Validate size (max 5MB)
        if (file.getSize() > 20 * 1024 * 1024) {
            throw new RuntimeException("File size should not exceed 20MB");
        }

        if (!contentType.equals("image/jpeg") &&
                !contentType.equals("image/png") &&
                !contentType.equals("image/jpg")) {

            throw new RuntimeException("Only JPG, JPEG, PNG allowed");
        }

        String url = supabaseUrl + "/storage/v1/object/" + bucket + "/" + fileName;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + supabaseKey);
        headers.set("apikey", supabaseKey);
        headers.setContentType(MediaType.IMAGE_JPEG);

        try {
            byte[] compressed = imageService.compressImage(file);

            HttpEntity<byte[]> request = new HttpEntity<>(compressed, headers);

            restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        } catch (Exception e) {
            throw new RuntimeException("Upload failed: " + e.getMessage());
        }

        return supabaseUrl + "/storage/v1/object/public/" + bucket + "/" + fileName;
    }
}