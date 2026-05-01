package com.n11.bootcamp.ecommerce.promotion.repository;

import com.n11.bootcamp.ecommerce.promotion.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    Optional<Promotion> findByCode(String code);

    boolean existsByCode(String code);

    List<Promotion> findAllByOrderByCreatedAtDesc();
}