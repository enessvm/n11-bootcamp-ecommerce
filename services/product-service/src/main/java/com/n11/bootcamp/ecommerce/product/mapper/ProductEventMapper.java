package com.n11.bootcamp.ecommerce.product.mapper;

import com.n11.bootcamp.ecommerce.events.record.Money;
import com.n11.bootcamp.ecommerce.events.product.ProductCreated;
import com.n11.bootcamp.ecommerce.product.entity.Product;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

/**
 * Converts product entities into wire-format event records.
 */
@Component
public class ProductEventMapper {

    public ProductCreated toCreatedEvent(Product p) {
        return new ProductCreated(
                UUID.randomUUID(),
                Instant.now(),
                p.getId(),
                p.getName(),
                p.getCategory().getId(),
                p.getBrand(),
                toEventMoney(p.getListPrice()),
                p.getCreatedAt()
        );
    }

    private Money toEventMoney(com.n11.bootcamp.ecommerce.product.entity.Money m) {
        return new Money(m.getAmount(), m.getCurrency());
    }
}