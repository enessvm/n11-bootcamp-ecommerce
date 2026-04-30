package com.n11.bootcamp.ecommerce.events.record;

import java.math.BigDecimal;

public record Money(
        BigDecimal amount,
        String currency
) {}