package com.n11.bootcamp.ecommerce.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateAddressRequest(

        @Size(max = 50)
        String label,

        @NotBlank
        String recipientName,

        @NotBlank
        String phoneNumber,

        @NotBlank @Size(max = 200)
        String line1,

        @Size(max = 200)
        String line2,

        @NotBlank
        String city,

        @NotBlank
        String postalCode,

        @NotBlank
        String country,

        boolean isDefault
) {}