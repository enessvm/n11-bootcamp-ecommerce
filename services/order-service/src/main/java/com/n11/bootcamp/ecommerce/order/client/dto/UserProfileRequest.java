package com.n11.bootcamp.ecommerce.order.client.dto;

import java.time.Instant;
import java.util.UUID;

public record UserProfileRequest(
        String phoneNumber,
        String identityNumber
) {}