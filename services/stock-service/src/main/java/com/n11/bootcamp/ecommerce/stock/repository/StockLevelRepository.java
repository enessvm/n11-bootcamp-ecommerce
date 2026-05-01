package com.n11.bootcamp.ecommerce.stock.repository;

import com.n11.bootcamp.ecommerce.stock.entity.StockLevel;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface StockLevelRepository extends JpaRepository<StockLevel, Long> {

    List<StockLevel> findAllByProductIdIn(Collection<Long> productIds);

    Optional<StockLevel> findByProductId(long productId);

    @Transactional
    @Modifying
    @Query(value = """
        INSERT INTO stock_level (product_id, available_quantity, reserved_quantity, created_at, updated_at)
        VALUES (:productId, 0, 0, :now, :now)
        ON CONFLICT (product_id) DO NOTHING
        """, nativeQuery = true)
    void insertIfAbsent(@Param("productId") Long productId, @Param("now") Instant now);

    /**
     * Pessimistic-write lock variant. Used by saga consumers (reserve, release, TTL expiration)
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM StockLevel s WHERE s.productId = :productId")
    Optional<StockLevel> findByProductIdForUpdate(@Param("productId") long productId);
}