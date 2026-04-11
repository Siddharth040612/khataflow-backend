package com.khataflow.repository;

import com.khataflow.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByPhoneAndIsDeletedFalse(String phone);

    Optional<User> findByPhoneAndStoreIdAndIsDeletedFalse(String phone, Long storeId);

    List<User> findByStoreIdAndIsDeletedFalse(Long storeId);
}