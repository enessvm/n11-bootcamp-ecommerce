package com.n11.bootcamp.ecommerce.promotion.exception;

import com.n11.bootcamp.ecommerce.web.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class PromotionNotFoundException extends BusinessException {

    public PromotionNotFoundException(Long id) {
        super("PROMOTION_NOT_FOUND", HttpStatus.NOT_FOUND,
                "Promotion with id " + id + " not found");
    }
}