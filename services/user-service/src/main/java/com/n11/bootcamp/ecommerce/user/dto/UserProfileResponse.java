package com.n11.bootcamp.ecommerce.user.dto;

import java.time.Instant;
import java.util.UUID;

public record UserProfileResponse(
        UUID keycloakSub,
        String displayName,
        String phoneNumber,
        String identityNumber
) {}