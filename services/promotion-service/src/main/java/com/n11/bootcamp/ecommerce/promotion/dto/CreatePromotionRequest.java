package com.n11.bootcamp.ecommerce.promotion.dto;

import com.n11.bootcamp.ecommerce.promotion.entity.DiscountType;
import com.n11.bootcamp.ecommerce.promotion.entity.PromotionScope;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.Instant;

public record CreatePromotionRequest(

        @NotBlank @Size(max = 50) @Pattern(regexp = "^[A-Z0-9_-]+$",
                message = "code must be uppercase letters, digits, underscore, or hyphen")
        String code,

        @NotBlank @Size(max = 100)
        String name,

        @NotNull
        DiscountType discountType,

        @NotNull @DecimalMin(value = "0.01", message = "discount value must be positive")
        BigDecimal discountValue,

        @NotNull
        PromotionScope scope,

        @PositiveOrZero
        BigDecimal minCartTotal,         // null = no minimum

        @Min(1)
        Integer maxUses,                 // null = unlimited

        @NotNull
        Instant validFrom,

        @NotNull @Future
        Instant validUntil
) {}