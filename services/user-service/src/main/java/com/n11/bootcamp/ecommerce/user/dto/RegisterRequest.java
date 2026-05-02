package com.n11.bootcamp.ecommerce.user.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

        @NotBlank
        @Email
        @Size(max = 254)
        String email,

        @NotBlank
        @Size(min = 8, max = 100)
        String password,

        @NotBlank
        String confirmPassword,

        @NotBlank
        @Size(min = 1, max = 50)
        String firstName,

        @NotBlank
        @Size(min = 1, max = 50)
        String lastName,

        @NotBlank
        @Pattern(regexp = "^\\+?[0-9 ]{7,20}$",
                message = "must be 7-20 digits, optionally prefixed with '+'")
        String phoneNumber,

        @NotBlank
        @Pattern(regexp = "^\\d{11}$", message = "must be exactly 11 digits")
        String identityNumber
) {

    @AssertTrue(message = "passwords do not match")
    public boolean isPasswordConfirmed() {
        return password != null && password.equals(confirmPassword);
    }
}
