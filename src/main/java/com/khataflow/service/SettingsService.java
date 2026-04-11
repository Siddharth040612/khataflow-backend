package com.khataflow.service;

import com.khataflow.dto.SettingsRequest;
import com.khataflow.entity.Settings;
import com.khataflow.repository.SettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SettingsService {

    private final SettingsRepository repository;

    public Settings save(SettingsRequest request) {

        Settings settings = repository.findByStoreId(request.getStoreId())
                .orElse(new Settings());

        settings.setStoreId(request.getStoreId());
        settings.setWhatsappTemplate(request.getWhatsappTemplate());
        settings.setIncludeBillDefault(
                request.getIncludeBillDefault() != null
                        ? request.getIncludeBillDefault()
                        : true
        );

        if (settings.getCreatedAt() == null) {
            settings.setCreatedAt(LocalDateTime.now());
        }

        settings.setUpdatedAt(LocalDateTime.now());

        return repository.save(settings);
    }

    public Settings get(Long storeId) {
        return repository.findByStoreId(storeId)
                .orElseGet(() -> defaultSettings(storeId));
    }

    private Settings defaultSettings(Long storeId) {

        Settings s = new Settings();
        s.setStoreId(storeId);

        s.setWhatsappTemplate(
                "Hi {name},\n\n" +
                        "Your pending balance is ₹{amount}.\n\n" +
                        "Pay here:\n{upiLink}\n\n" +
                        "{bills}\n\n" +
                        "Thanks,\n{storeName}"
        );

        s.setIncludeBillDefault(true);
        s.setCreatedAt(LocalDateTime.now());

        return repository.save(s);
    }
}