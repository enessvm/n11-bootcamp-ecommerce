package com.n11.bootcamp.ecommerce.promotion.dto;

import com.n11.bootcamp.ecommerce.promotion.entity.DiscountType;
import com.n11.bootcamp.ecommerce.promotion.entity.PromotionScope;

import java.math.BigDecimal;
import java.time.Instant;

public record PromotionResponse(
        Long id,
        String code,
        String name,
        DiscountType discountType,
        BigDecimal discountValue,
        PromotionScope scope,
        BigDecimal minCartTotal,
        Integer maxUses,
        int timesRedeemed,
        Instant validFrom,
        Instant validUntil,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {}