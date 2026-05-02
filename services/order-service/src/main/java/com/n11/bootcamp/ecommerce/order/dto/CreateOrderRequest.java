package com.n11.bootcamp.ecommerce.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateOrderRequest(

        @NotEmpty @Valid
        List<CreateOrderRequestLineItem> lineItems,

        @Size(max = 50)
        String appliedCouponCode,

        @NotNull @Valid
        AddressRequest shippingAddress,

        @NotNull @Valid
        AddressRequest billingAddress,

        @NotBlank @Size(max = 50)
        String paymentProvider
) {}