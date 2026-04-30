package com.n11.bootcamp.ecommerce.events.product;

import com.n11.bootcamp.ecommerce.events.record.Money;

import java.time.Instant;
import java.util.UUID;

/**
 * Emitted by {@code product-service} on successful {@code POST /products}.
 *
 * <p>Routing key: {@code product.created}. Exchange: {@code product.events}.
 *
 * <p>Consumers:
 * <ul>
 *   <li>{@code stock-service} — initialize a {@code stock_level} row at
 *       quantity 0.</li>
 * </ul>
 *
 * <p>{@code occurredAt} is when the event was emitted; {@code createdAt}
 * is when the underlying product row was created. Equal on first emission;
 * future replays may diverge them.
 */
public record ProductCreated(
        UUID eventId,
        Instant occurredAt,
        long productId,
        String name,
        long categoryId,
        String brand,
        Money listPrice,
        Instant createdAt
) {}