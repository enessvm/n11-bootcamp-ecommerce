package com.n11.bootcamp.ecommerce.order.service;

import com.n11.bootcamp.ecommerce.order.dto.CreateOrderRequest;
import com.n11.bootcamp.ecommerce.order.dto.OrderResponse;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.UUID;

public interface OrderService {

    OrderResponse createOrder(Jwt jwt, String ipAddress, CreateOrderRequest request);

    OrderResponse getById(Long id, UUID userId);
}