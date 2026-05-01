package com.n11.bootcamp.ecommerce.order.entity;

public enum SagaState {
    INITIATED,
    STOCK_RESERVED,
    STOCK_FAILED,
    PROMOTION_APPLIED,
    PROMOTION_FAILED,
    COMPLETED,
    FAILED
}