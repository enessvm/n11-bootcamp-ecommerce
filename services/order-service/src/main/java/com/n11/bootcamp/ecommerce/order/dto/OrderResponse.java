package com.n11.bootcamp.ecommerce.order.dto;

import com.n11.bootcamp.ecommerce.order.entity.Money;
import com.n11.bootcamp.ecommerce.order.entity.SagaState;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
        Long id,
        UUID userId,
        UUID sagaId,
        SagaState sagaState,
        String failureReason,
        String appliedCouponCode,
        AddressResponse shippingAddress,
        AddressResponse billingAddress,
        Money subtotal,
        Money cartTotalDiscount,
        Money total,
        List<OrderLineItemResponse> lineItems,
        Instant createdAt,
        Instant updatedAt,
        BuyerResponse buyer,
        String paymentProvider,
        String paymentPageUrl
) {}