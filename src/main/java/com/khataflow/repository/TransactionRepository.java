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
import com.khataflow.dto.RecentTransaction;

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
AND t.createdAt >= :fromDate
AND t.createdAt <= :toDate
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

    @Query("""
        SELECT COALESCE(SUM(CASE WHEN t.type = 'CREDIT' THEN t.amount ELSE 0 END), 0)
        FROM Transaction t
        WHERE t.storeId = :storeId
        AND t.isDeleted = false
    """)
    Double getTotalReceivable(@Param("storeId") Long storeId);

    @Query("""
        SELECT COALESCE(SUM(CASE WHEN t.type = 'PAYMENT' THEN t.amount ELSE 0 END), 0)
        FROM Transaction t
        WHERE t.storeId = :storeId
        AND t.isDeleted = false
    """)
    Double getTotalPayable(@Param("storeId") Long storeId);

    @Query("""
        SELECT new com.khataflow.dto.RecentTransaction(
            t.id,
            p.name,
            t.amount,
            t.type,
            t.createdAt
        )
        FROM Transaction t
        JOIN Party p ON t.partyId = p.id
        WHERE t.storeId = :storeId
        AND t.isDeleted = false
        ORDER BY t.createdAt DESC
    """)
    List<RecentTransaction> findRecentTransactions(@Param("storeId") Long storeId, Pageable pageable);

    @Query("""
        SELECT 
            COALESCE(SUM(CASE WHEN t.type = 'CREDIT' THEN t.amount ELSE 0 END), 0),
            COALESCE(SUM(CASE WHEN t.type = 'PAYMENT' THEN t.amount ELSE 0 END), 0),
            COUNT(*)
        FROM Transaction t
        WHERE t.storeId = :storeId
        AND t.isDeleted = false
        AND t.createdAt >= :startDate
        AND t.createdAt <= :endDate
    """)
    Object[] getTransactionSummaryByDateRange(
            @Param("storeId") Long storeId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("""
        SELECT 
            COALESCE(SUM(CASE WHEN t.type = 'CREDIT' THEN t.amount ELSE 0 END), 0),
            COALESCE(SUM(CASE WHEN t.type = 'PAYMENT' THEN t.amount ELSE 0 END), 0),
            COUNT(t)
        FROM Transaction t
        WHERE t.storeId = :storeId
        AND t.partyId = :partyId
        AND t.isDeleted = false
    """)
    Object[] getPartyStats(
            @Param("storeId") Long storeId,
            @Param("partyId") Long partyId
    );

    @Query("""
        SELECT MAX(t.createdAt)
        FROM Transaction t
        WHERE t.storeId = :storeId
        AND t.partyId = :partyId
        AND t.isDeleted = false
    """)
    LocalDateTime getLastTransactionDate(
            @Param("storeId") Long storeId,
            @Param("partyId") Long partyId
    );

    @Query(value = """
        SELECT p.id, p.name, COUNT(t.id) as transactionCount
        FROM parties p
        LEFT JOIN transactions t ON p.id = t.party_id 
            AND t.store_id = :storeId 
            AND t.is_deleted = false
        WHERE p.store_id = :storeId
        GROUP BY p.id, p.name
        ORDER BY transactionCount DESC
        LIMIT 5
    """, nativeQuery = true)
    List<Object[]> findFrequentParties(@Param("storeId") Long storeId);

    @Query("""
        SELECT COUNT(DISTINCT t.partyId)
        FROM Transaction t
        WHERE t.storeId = :storeId
        AND t.isDeleted = false
    """)
    Long countActiveParties(@Param("storeId") Long storeId);
}

