package com.n11.bootcamp.ecommerce.order.service;

import com.n11.bootcamp.ecommerce.order.dto.CreateOrderRequest;
import com.n11.bootcamp.ecommerce.order.dto.OrderResponse;

import java.util.UUID;

public interface OrderService {

    OrderResponse createOrder(UUID userId, CreateOrderRequest request);

    OrderResponse getById(Long id, UUID userId);
}