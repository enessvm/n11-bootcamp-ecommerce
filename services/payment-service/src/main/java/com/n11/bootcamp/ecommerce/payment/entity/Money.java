package com.n11.bootcamp.ecommerce.payment.entity;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Embeddable
@Getter
@Setter
@NoArgsConstructor()
@AllArgsConstructor
public class Money {

    @NotNull
    @DecimalMin(value = "0.00")
    private BigDecimal amount;

    @NotBlank
    @Size(min = 3, max = 3)
    private String currency;
}