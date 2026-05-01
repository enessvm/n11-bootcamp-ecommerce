package com.n11.bootcamp.ecommerce.promotion.mapper;

import com.n11.bootcamp.ecommerce.promotion.dto.*;
import com.n11.bootcamp.ecommerce.promotion.entity.Promotion;
import org.springframework.stereotype.Component;

@Component
public class PromotionMapper {

    public Promotion toEntity(CreatePromotionRequest request) {
        Promotion promotion = new Promotion();
        promotion.setCode(request.code().toUpperCase()); // defensive normalization
        promotion.setName(request.name());
        promotion.setDiscountType(request.discountType());
        promotion.setDiscountValue(request.discountValue());
        promotion.setScope(request.scope());
        promotion.setMinCartTotal(request.minCartTotal());
        promotion.setMaxUses(request.maxUses());
        promotion.setValidFrom(request.validFrom());
        promotion.setValidUntil(request.validUntil());
        promotion.setActive(true); // new promos default to active
        return promotion;
    }

    public void updateEntity(Promotion existing, UpdatePromotionRequest request) {
        existing.setName(request.name());
        existing.setDiscountType(request.discountType());
        existing.setDiscountValue(request.discountValue());
        existing.setScope(request.scope());
        existing.setMinCartTotal(request.minCartTotal());
        existing.setMaxUses(request.maxUses());
        existing.setValidFrom(request.validFrom());
        existing.setValidUntil(request.validUntil());
        existing.setActive(request.active());
    }

    public PromotionResponse toResponse(Promotion p) {
        return new PromotionResponse(
                p.getId(),
                p.getCode(),
                p.getName(),
                p.getDiscountType(),
                p.getDiscountValue(),
                p.getScope(),
                p.getMinCartTotal(),
                p.getMaxUses(),
                p.getTimesRedeemed(),
                p.getValidFrom(),
                p.getValidUntil(),
                p.isActive(),
                p.getCreatedAt(),
                p.getUpdatedAt()
        );
    }

    public PromotionValidationResponse toValidResponse(Promotion p) {
        return PromotionValidationResponse.valid(
                p.getCode(),
                p.getDiscountType(),
                p.getDiscountValue(),
                p.getScope(),
                p.getMinCartTotal()
        );
    }

    public PromotionValidationResponse toInvalidResponse(String code, ValidationFailure reason) {
        return PromotionValidationResponse.invalid(code, reason);
    }
}