package com.khataflow.service;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;

@Service
public class ImageService {

    public byte[] compressImage(MultipartFile file) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            Thumbnails.of(file.getInputStream())
                    .size(1200, 1200)      // 🔥 increased
                    .outputQuality(0.85)   // 🔥 better clarity
                    .outputFormat("jpg")
                    .toOutputStream(outputStream);

            return outputStream.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Image compression failed: " + e.getMessage());
        }
    }
}