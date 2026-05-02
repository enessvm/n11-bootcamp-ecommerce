package com.n11.bootcamp.ecommerce.user.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        long expiresIn,
        long refreshExpiresIn,
        String tokenType,
        UserProfileResponse profile
) {}
