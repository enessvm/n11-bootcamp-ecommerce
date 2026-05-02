package com.n11.bootcamp.ecommerce.user.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateUserProfileRequest(

        @Size(max = 100)
        String displayName,

        String phoneNumber,

        String identityNumber
) {}