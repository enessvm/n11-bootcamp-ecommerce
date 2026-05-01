package com.n11.bootcamp.ecommerce.user.dto;

import java.time.Instant;

public record AddressResponse(
        Long id,
        String label,
        String recipientName,
        String phoneNumber,
        String line1,
        String line2,
        String city,
        String postalCode,
        String country,
        boolean isDefault,
        Instant createdAt,
        Instant updatedAt
) {}