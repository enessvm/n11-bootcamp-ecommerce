package com.n11.bootcamp.ecommerce.promotion.dto;

import com.n11.bootcamp.ecommerce.promotion.entity.DiscountType;
import com.n11.bootcamp.ecommerce.promotion.entity.PromotionScope;

import java.math.BigDecimal;

/**
 * Response for {@code GET /promotions/validate/{code}}. Same shape for valid
 * and invalid cases — frontend reads {@code valid} and either applies the
 * discount details or shows the {@code reason}.
 *
 * <p>For invalid codes the discount fields are all null. For valid codes
 * {@code reason} is null.
 */
public record PromotionValidationResponse(
        String code,
        boolean valid,
        DiscountType discountType,
        BigDecimal discountValue,
        PromotionScope scope,
        BigDecimal minCartTotal,
        ValidationFailure reason
) {

    public static PromotionValidationResponse valid(String code,
                                                    DiscountType type,
                                                    BigDecimal value,
                                                    PromotionScope scope,
                                                    BigDecimal minCartTotal) {
        return new PromotionValidationResponse(code, true, type, value, scope, minCartTotal, null);
    }

    public static PromotionValidationResponse invalid(String code, ValidationFailure reason) {
        return new PromotionValidationResponse(code, false, null, null, null, null, reason);
    }
}