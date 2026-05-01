package com.n11.bootcamp.ecommerce.promotion.dto;

import com.n11.bootcamp.ecommerce.promotion.entity.DiscountType;
import com.n11.bootcamp.ecommerce.promotion.entity.PromotionScope;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.Instant;

public record UpdatePromotionRequest(

        @NotBlank @Size(max = 100)
        String name,

        @NotNull
        DiscountType discountType,

        @NotNull @DecimalMin(value = "0.01")
        BigDecimal discountValue,

        @NotNull
        PromotionScope scope,

        @PositiveOrZero
        BigDecimal minCartTotal,

        @Min(1)
        Integer maxUses,

        @NotNull
        Instant validFrom,

        @NotNull
        Instant validUntil,

        boolean active
) {}