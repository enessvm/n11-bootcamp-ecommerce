package com.n11.bootcamp.ecommerce.order.entity;

public enum SagaState {
    INITIATED,
    STOCK_RESERVED,
    PROMOTION_APPLIED,
    PROMOTION_FAILED,
    COMMIT_REQUESTED,
    COMPENSATING_STOCK,
    COMPLETED,
    FAILED
}