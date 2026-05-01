package com.n11.bootcamp.ecommerce.order.dto;

public record AddressResponse(
        String recipientName,
        String phoneNumber,
        String line1,
        String line2,
        String city,
        String postalCode,
        String country
) {}