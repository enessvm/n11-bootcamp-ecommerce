package com.n11.bootcamp.ecommerce.promotion.exception;

import com.n11.bootcamp.ecommerce.web.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class DuplicatePromotionCodeException extends BusinessException {

    public DuplicatePromotionCodeException(String code) {
        super("DUPLICATE_PROMOTION_CODE", HttpStatus.CONFLICT,
                "Promotion with code '" + code + "' already exists");
    }
}