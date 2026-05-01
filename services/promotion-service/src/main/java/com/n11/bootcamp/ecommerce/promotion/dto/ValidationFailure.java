package com.n11.bootcamp.ecommerce.promotion.dto;

public enum ValidationFailure {
    NOT_FOUND,
    INACTIVE,
    NOT_YET_VALID,
    EXPIRED,
    MAX_USES_REACHED,
    CART_BELOW_MINIMUM
}