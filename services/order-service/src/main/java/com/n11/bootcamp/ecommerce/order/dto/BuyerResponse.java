package com.n11.bootcamp.ecommerce.order.dto;

public record BuyerResponse(
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        String identityNumber,
        String ipAddress
) {}