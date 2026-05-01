package com.khataflow.repository;

import com.khataflow.dto.TransactionResponse;
import com.khataflow.entity.Transaction;
import com.khataflow.entity.TransactionType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.*;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // ✅ MAIN QUERY (JOIN + DTO)
    @Query("""
        SELECT new com.khataflow.dto.TransactionResponse(
            t.id,
            p.id,
            p.name,
            t.type,
            t.amount,
            t.billNumber,
            t.description,
            t.createdAt,
            t.createdBy,
            u.name
        )
        FROM Transaction t
        JOIN Party p ON t.partyId = p.id
        LEFT JOIN User u ON t.createdBy = u.id
        WHERE t.storeId = :storeId
        AND t.partyId = :partyId
        AND t.isDeleted = false
        ORDER BY t.createdAt DESC
    """)
    List<TransactionResponse> getTransactions(
            @Param("storeId") Long storeId,
            @Param("partyId") Long partyId
    );


    // ✅ BALANCE (DB AGGREGATION - FAST)
    @Query("""
        SELECT 
            COALESCE(SUM(CASE WHEN t.type = 'CREDIT' THEN t.amount ELSE 0 END), 0),
            COALESCE(SUM(CASE WHEN t.type = 'PAYMENT' THEN t.amount ELSE 0 END), 0)
        FROM Transaction t
        WHERE t.storeId = :storeId
        AND t.partyId = :partyId
        AND t.isDeleted = false
    """)
    Object[] getBalance(
            @Param("storeId") Long storeId,
            @Param("partyId") Long partyId
    );

    Optional<Transaction> findByIdAndStoreId(Long id, Long storeId);

    @Query("""
    SELECT t FROM Transaction t
    WHERE t.storeId = :storeId
    AND t.partyId = :partyId
    AND t.isDeleted = false
    AND (:type IS NULL OR t.type = :type)
    AND (:billNumber IS NULL OR t.billNumber LIKE :billNumber)
    AND (:fromDate IS NULL OR t.createdAt >= :fromDate)
    AND (:toDate IS NULL OR t.createdAt <= :toDate)
    ORDER BY t.createdAt DESC
""")
    List<Transaction> findFilteredTransactions(
            Long storeId,
            Long partyId,
            TransactionType type,
            String billNumber,
            LocalDateTime fromDate,
            LocalDateTime toDate
    );

    List<Transaction> findByStoreIdAndPartyIdAndIsDeletedFalseOrderByCreatedAtDesc(
            Long storeId, Long partyId
    );

    List<Transaction> findByStoreIdAndPartyIdAndIsDeletedFalse(Long storeId, Long partyId);

    @Query("""
    SELECT new com.khataflow.dto.TransactionResponse(
        t.id,
        p.id,
        p.name,
        t.type,
        t.amount,
        t.billNumber,
        b.fileUrl,
        t.description,
        t.createdAt,
        t.createdBy,
        u.name,
        null
    )
    FROM Transaction t
    JOIN Party p ON t.partyId = p.id
    LEFT JOIN User u ON t.createdBy = u.id
    LEFT JOIN Bill b ON t.billId = b.id
    WHERE t.storeId = :storeId
    AND t.partyId = :partyId
    AND t.isDeleted = false
    ORDER BY t.createdAt DESC
""")
    List<TransactionResponse> getTransactionsWithFileurl(
            @Param("storeId") Long storeId,
            @Param("partyId") Long partyId
    );

    @Query("""
        SELECT t, b FROM Transaction t
        LEFT JOIN Bill b ON t.billId = b.id
        WHERE t.storeId = :storeId
        AND t.partyId = :partyId
        AND t.isDeleted = false
        ORDER BY t.createdAt DESC
    """)
    List<Object[]> findTransactionsWithBills(
            @Param("storeId") Long storeId,
            @Param("partyId") Long partyId
    );

    @Query("""
SELECT t FROM Transaction t
WHERE t.storeId = :storeId
AND t.partyId = :partyId
AND t.isDeleted = false
AND (:fromDate IS NULL OR t.createdAt >= :fromDate)
AND (:toDate IS NULL OR t.createdAt <= :toDate)
ORDER BY t.createdAt DESC
""")
    Page<Transaction> findFilteredTransactionsPaged(
            @Param("storeId") Long storeId,
            @Param("partyId") Long partyId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable
    );

    Page<Transaction> findByStoreIdAndPartyIdAndIsDeletedFalse(
            Long storeId,
            Long partyId,
            Pageable pageable
    );
}