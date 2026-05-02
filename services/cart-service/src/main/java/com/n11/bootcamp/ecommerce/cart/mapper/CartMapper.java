package com.n11.bootcamp.ecommerce.cart.mapper;

import com.n11.bootcamp.ecommerce.cart.client.dto.ProductBatchResponse;
import com.n11.bootcamp.ecommerce.cart.dto.CartResponse;
import com.n11.bootcamp.ecommerce.cart.dto.Money;
import com.n11.bootcamp.ecommerce.cart.entity.Cart;
import com.n11.bootcamp.ecommerce.cart.entity.CartItem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Component
public class CartMapper {

    private static final String DEFAULT_CURRENCY = "TRY";

    public CartResponse toResponse(Cart cart, Map<Long, ProductBatchResponse.BatchEntry> productsById) {
        List<CartResponse.Item> items = cart.getLineItems().stream()
                .map(item -> toItem(item, productsById.get(item.getProductId())))
                .toList();

        Money subtotal = computeSubtotal(items);

        return new CartResponse(
                cart.getId(),
                items,
                subtotal,
                cart.getUpdatedAt()
        );
    }

    private CartResponse.Item toItem(CartItem item, ProductBatchResponse.BatchEntry product) {
        // product may be missing (deleted upstream after add).
        // Render placeholder values rather than fail the whole cart read.
        String name = product != null ? product.name() : "(unavailable)";
        String brand = product != null ? product.brand() : null;
        String image = product != null ? product.primaryImageUrl() : null;
        Money unitPrice = product != null
                ? product.listPrice()
                : new Money(BigDecimal.ZERO, DEFAULT_CURRENCY);

        BigDecimal lineAmount = unitPrice.amount().multiply(BigDecimal.valueOf(item.getQuantity()));
        Money lineTotal = new Money(lineAmount, unitPrice.currency());

        return new CartResponse.Item(
                item.getId(),
                item.getProductId(),
                name,
                brand,
                image,
                unitPrice,
                item.getQuantity(),
                lineTotal
        );
    }

    private Money computeSubtotal(List<CartResponse.Item> items) {
        if (items.isEmpty()) {
            return new Money(BigDecimal.ZERO, DEFAULT_CURRENCY);
        }

        // Single-currency platform — first item's currency drives the cart.
        String currency = items.get(0).unitPrice().currency();
        BigDecimal total = items.stream()
                .map(it -> it.lineTotal().amount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new Money(total, currency);
    }
}