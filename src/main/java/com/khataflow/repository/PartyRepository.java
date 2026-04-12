package com.khataflow.repository;

import com.khataflow.entity.Party;
import com.khataflow.entity.PartyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PartyRepository extends JpaRepository<Party, Long>, JpaSpecificationExecutor<Party> {

    Optional<Party> findByIdAndStoreId(Long id, Long storeId);


    List<Party> findByStoreId(Long storeId);

    List<Party> findByStoreIdAndPartyType(Long storeId, PartyType partyType);

    List<Party> findByStoreIdAndNameContainingIgnoreCase(Long storeId, String name);

}