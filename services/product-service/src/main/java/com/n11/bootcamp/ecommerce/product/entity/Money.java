package com.n11.bootcamp.ecommerce.product.entity;

import jakarta.persistence.Embeddable;
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

    private BigDecimal amount;
    private String currency;
}