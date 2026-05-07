package com.khataflow.service;

import com.khataflow.dto.CreateStoreRequest;
import com.khataflow.dto.StoreResponse;
import com.khataflow.entity.*;
import com.khataflow.repository.StoreRepository;
import com.khataflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    public Long createStore(CreateStoreRequest request) {

        // 🏬 Create Store
        Store store = new Store();
        store.setName(request.getStoreName());
        store.setPhone(request.getStorePhone());
        store.setUpiId(request.getUpiId());
        store.setCreatedAt(LocalDateTime.now());

        Store savedStore = storeRepository.save(store);

        // 👤 Create Admin User
        User user = new User();
        user.setStoreId(savedStore.getId());
        user.setName(request.getAdminName());
        user.setPhone(request.getAdminPhone());
        user.setEmail(request.getAdminEmail());

        user.setRole(UserRole.ADMIN);
        user.setStatus(UserStatus.ACTIVE);

        user.setCreatedAt(LocalDateTime.now());
        user.setCreatedBy(savedStore.getId()); // first user

        userRepository.save(user);

        return savedStore.getId();
    }

    public StoreResponse getStoreDetails(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("Store not found"));

        return new StoreResponse(
                store.getId(),
                store.getName(),
                store.getPhone(),
                store.getUpiId(),
                store.getCurrency(),
                store.getIsActive(),
                store.getCreatedAt()
        );
    }
}