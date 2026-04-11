package com.khataflow.repository;

import com.khataflow.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BillRepository extends JpaRepository<Bill, Long> {

    List<Bill> findTop5ByStoreIdAndPartyIdOrderByCreatedAtDesc(Long storeId, Long partyId);
}