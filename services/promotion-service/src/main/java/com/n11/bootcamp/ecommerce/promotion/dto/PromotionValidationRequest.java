package com.n11.bootcamp.ecommerce.promotion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record PromotionValidationRequest(

        @NotBlank @Size(max = 50)
        @Pattern(regexp = "^[A-Za-z0-9_-]+$",
                message = "Coupon code may only contain letters, digits, underscore, hyphen")
        String code,

        @NotNull @Positive
        BigDecimal cartTotal,

        @NotBlank @Size(min = 3, max = 3)
        String currency
) {}