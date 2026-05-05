package com.khataflow.repository;

import com.khataflow.entity.Party;
import com.khataflow.entity.PartyType;
import org.springframework.data.domain.Pageable;
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

    @Query("""
        SELECT p FROM Party p
        WHERE p.storeId = :storeId
        ORDER BY p.updatedAt DESC
    """)
    List<Party> findRecentParties(@Param("storeId") Long storeId, Pageable pageable);

    @Query("""
        SELECT p FROM Party p
        WHERE p.storeId = :storeId
        ORDER BY (
            SELECT MAX(t.createdAt)
            FROM Transaction t
            WHERE t.partyId = p.id
            AND t.storeId = :storeId
            AND t.isDeleted = false
        ) DESC NULLS LAST
    """)
    List<Party> findPartiesByLastTransactionDate(@Param("storeId") Long storeId, Pageable pageable);

    @Query("""
        SELECT COUNT(p)
        FROM Party p
        WHERE p.storeId = :storeId
    """)
    long countByStoreId(@Param("storeId") Long storeId);
}