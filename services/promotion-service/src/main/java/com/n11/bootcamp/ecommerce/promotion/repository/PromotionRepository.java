package com.n11.bootcamp.ecommerce.promotion.repository;

import com.n11.bootcamp.ecommerce.promotion.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    Optional<Promotion> findByCode(String code);

    boolean existsByCode(String code);

    List<Promotion> findAllByOrderByCreatedAtDesc();

    /**
     * Increment with cap check. Returns 1 if the row was updated
     * (slot acquired), 0 if the cap was already reached or the promotion
     * doesn't exist.
     */
    @Modifying
    @Query(value = """
        UPDATE promotion
        SET times_redeemed = times_redeemed + 1
        WHERE id = :id
          AND active = true
          AND (max_uses IS NULL OR times_redeemed < max_uses)
        """, nativeQuery = true)
    int tryIncrementTimesRedeemed(@Param("id") Long id);
}