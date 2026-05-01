package com.n11.bootcamp.ecommerce.promotion.repository;

import com.n11.bootcamp.ecommerce.promotion.entity.PromotionRedemption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PromotionRedemptionRepository extends JpaRepository<PromotionRedemption, Long> {

    Optional<PromotionRedemption> findBySagaId(UUID sagaId);
}