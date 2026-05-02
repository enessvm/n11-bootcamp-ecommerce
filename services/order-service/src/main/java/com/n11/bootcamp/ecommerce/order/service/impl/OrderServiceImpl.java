package com.n11.bootcamp.ecommerce.order.service.impl;

import com.n11.bootcamp.ecommerce.order.client.ProductServiceClient;
import com.n11.bootcamp.ecommerce.order.client.UserServiceClient;
import com.n11.bootcamp.ecommerce.order.client.dto.ProductBatchEntry;
import com.n11.bootcamp.ecommerce.order.client.dto.ProductBatchRequest;
import com.n11.bootcamp.ecommerce.order.client.dto.ProductBatchResponse;
import com.n11.bootcamp.ecommerce.order.client.dto.UserProfileRequest;
import com.n11.bootcamp.ecommerce.order.dto.CreateOrderRequest;
import com.n11.bootcamp.ecommerce.order.dto.CreateOrderRequestLineItem;
import com.n11.bootcamp.ecommerce.order.dto.OrderResponse;
import com.n11.bootcamp.ecommerce.order.entity.Buyer;
import com.n11.bootcamp.ecommerce.order.entity.Order;
import com.n11.bootcamp.ecommerce.order.exception.OrderNotFoundException;
import com.n11.bootcamp.ecommerce.order.exception.ProfileIncompleteException;
import com.n11.bootcamp.ecommerce.order.mapper.OrderMapper;
import com.n11.bootcamp.ecommerce.order.repository.OrderRepository;
import com.n11.bootcamp.ecommerce.order.service.OrderService;
import com.n11.bootcamp.ecommerce.web.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final ProductServiceClient productServiceClient;
    private final UserServiceClient userServiceClient;
    private final SagaServiceImpl sagaService;

    @Override
    @Transactional
    public OrderResponse createOrder(Jwt jwt, String ipAddress, CreateOrderRequest request) {
        UUID userId = UUID.fromString(jwt.getSubject());

        // 0. Profile completeness gate. Cheaper than the product batch fetch,
        //    so run it first.
        UserProfileRequest profile = userServiceClient.getCurrentProfile();
        if (isBlank(profile.phoneNumber())) {
            throw new ProfileIncompleteException("phoneNumber");
        }
        if (isBlank(profile.identityNumber())) {
            throw new ProfileIncompleteException("identityNumber");
        }

        Buyer buyer = new Buyer(
                jwt.getClaimAsString("given_name"),
                jwt.getClaimAsString("family_name"),
                jwt.getClaimAsString("email"),
                profile.phoneNumber(),
                profile.identityNumber(),
                ipAddress
        );

        // 1. Snapshot product details from product-service.
        List<Long> productIds = request.lineItems().stream()
                .map(CreateOrderRequestLineItem::productId)
                .toList();

        ProductBatchResponse batchResponse = productServiceClient.batch(
                new ProductBatchRequest(productIds));

        // 2. Validate every requested productId came back from product-service.
        Set<Long> returnedIds = batchResponse.products().stream()
                .map(ProductBatchEntry::id)
                .collect(Collectors.toSet());

        for (Long requestedId : productIds) {
            if (!returnedIds.contains(requestedId)) {
                throw new BusinessException("PRODUCT_NOT_FOUND", HttpStatus.BAD_REQUEST,
                        "Product with id " + requestedId + " not found") {};
            }
        }

        // 3. Build order entity, persist in INITIATED state.
        Order order = orderMapper.toEntity(request, userId, buyer, batchResponse.products());
        Order saved = orderRepository.save(order);
        log.info("Order created orderId={} userId={} sagaId={} state=INITIATED",
                saved.getId(), saved.getUserId(), saved.getSagaId());

        // 4. Kick off the saga.
        sagaService.start(saved);

        return orderMapper.toResponse(saved);
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getById(Long id, UUID userId) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        if (!order.getUserId().equals(userId)) {
            throw new OrderNotFoundException(id);
        }

        return orderMapper.toResponse(order);
    }
}