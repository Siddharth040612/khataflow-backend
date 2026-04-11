package com.khataflow.service;

import com.khataflow.dto.CreateUserRequest;
import com.khataflow.entity.User;
import com.khataflow.entity.UserStatus;
import com.khataflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    // 🔥 CREATE USER
    public User create(CreateUserRequest request) {

        if (request.getStoreId() == null) {
            throw new RuntimeException("storeId is required");
        }

        if (request.getPhone() == null || request.getPhone().isBlank()) {
            throw new RuntimeException("phone is required");
        }

        // 🔒 Prevent duplicate
        if (repository.findByPhoneAndStoreIdAndIsDeletedFalse(
                request.getPhone(), request.getStoreId()).isPresent()) {
            throw new RuntimeException("User already exists");
        }

        User user = new User();
        user.setStoreId(request.getStoreId());
        user.setName(request.getName());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());

        user.setRole(request.getRole());
        user.setStatus(UserStatus.ACTIVE);

        user.setIsDeleted(false);
        user.setCreatedAt(LocalDateTime.now());

        return repository.save(user);
    }

    // 🔥 GET USERS
    public List<User> getUsers(Long storeId) {
        return repository.findByStoreIdAndIsDeletedFalse(storeId);
    }

    // 🔥 GET BY ID
    public User getById(Long id, Long storeId) {
        return repository.findById(id)
                .filter(u -> u.getStoreId().equals(storeId))
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // 🔥 DELETE (SOFT)
    public void delete(Long id, Long storeId) {

        User user = repository.findById(id)
                .filter(u -> u.getStoreId().equals(storeId))
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setIsDeleted(true);
        repository.save(user);
    }

    // 🔥 LOGIN SUPPORT
    public User getByPhone(String phone) {
        User user = repository.findByPhoneAndIsDeletedFalse(phone)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new RuntimeException("User not active");
        }

        return user;
    }
}