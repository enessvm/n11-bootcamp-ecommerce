package com.n11.bootcamp.ecommerce.order.mapper;

import com.n11.bootcamp.ecommerce.order.client.dto.ProductBatchEntry;
import com.n11.bootcamp.ecommerce.order.dto.AddressRequest;
import com.n11.bootcamp.ecommerce.order.dto.AddressResponse;
import com.n11.bootcamp.ecommerce.order.dto.CreateOrderRequest;
import com.n11.bootcamp.ecommerce.order.dto.CreateOrderRequestLineItem;
import com.n11.bootcamp.ecommerce.order.dto.OrderLineItemResponse;
import com.n11.bootcamp.ecommerce.order.dto.OrderResponse;
import com.n11.bootcamp.ecommerce.order.entity.Address;
import com.n11.bootcamp.ecommerce.order.entity.Money;
import com.n11.bootcamp.ecommerce.order.entity.Order;
import com.n11.bootcamp.ecommerce.order.entity.OrderLineItem;
import com.n11.bootcamp.ecommerce.order.entity.SagaState;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

    /**
     * Build an Order entity from a request, snapshotting product details
     * from product-service's batch response.
     *
     * <p>Saga starts in {@code INITIATED}. Subtotal = sum of line totals.
     * Cart total discount = zero in slice 1a (no promotion-service yet).
     * Total = subtotal.
     */
    public Order toEntity(CreateOrderRequest request,
                          UUID userId,
                          List<ProductBatchEntry> productSnapshots) {

        Map<Long, ProductBatchEntry> snapshotsByProductId = productSnapshots.stream()
                .collect(Collectors.toMap(ProductBatchEntry::id, Function.identity()));

        Order order = new Order();
        order.setUserId(userId);
        order.setSagaId(UUID.randomUUID());
        order.setSagaState(SagaState.INITIATED);
        order.setAppliedCouponCode(request.appliedCouponCode());
        order.setShippingAddress(toAddress(request.shippingAddress()));
        order.setBillingAddress(toAddress(request.billingAddress()));

        BigDecimal subtotalAmount = BigDecimal.ZERO;
        String currency = null;

        for (CreateOrderRequestLineItem requested : request.lineItems()) {
            ProductBatchEntry snapshot = snapshotsByProductId.get(requested.productId());

            OrderLineItem line = new OrderLineItem();
            line.setProductId(snapshot.id());
            line.setProductName(snapshot.name());
            line.setProductBrand(snapshot.brand());
            line.setPrimaryImageUrl(snapshot.primaryImageUrl());
            line.setQuantity(requested.quantity());

            Money unitListPrice = copyMoney(snapshot.listPrice());
            line.setUnitListPrice(unitListPrice);
            line.setUnitEffectivePrice(copyMoney(unitListPrice));

            BigDecimal lineTotalAmount = unitListPrice.getAmount()
                    .multiply(BigDecimal.valueOf(requested.quantity()));
            line.setLineTotal(new Money(lineTotalAmount, unitListPrice.getCurrency()));

            order.addLineItem(line);

            subtotalAmount = subtotalAmount.add(lineTotalAmount);
            currency = unitListPrice.getCurrency();
        }

        order.setSubtotal(new Money(subtotalAmount, currency));
        order.setCartTotalDiscount(new Money(BigDecimal.ZERO, currency));
        order.setTotal(new Money(subtotalAmount, currency));

        return order;
    }

    public OrderResponse toResponse(Order order) {
        List<OrderLineItemResponse> items = order.getLineItems().stream()
                .map(this::toLineItemResponse)
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getUserId(),
                order.getSagaId(),
                order.getSagaState(),
                order.getFailureReason(),
                order.getAppliedCouponCode(),
                toAddressResponse(order.getShippingAddress()),
                toAddressResponse(order.getBillingAddress()),
                order.getSubtotal(),
                order.getCartTotalDiscount(),
                order.getTotal(),
                items,
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }

    private OrderLineItemResponse toLineItemResponse(OrderLineItem item) {
        return new OrderLineItemResponse(
                item.getId(),
                item.getProductId(),
                item.getProductName(),
                item.getProductBrand(),
                item.getPrimaryImageUrl(),
                item.getQuantity(),
                item.getUnitListPrice(),
                item.getUnitEffectivePrice(),
                item.getLineTotal(),
                item.getAppliedPromotionCode()
        );
    }

    private Address toAddress(AddressRequest request) {
        Address address = new Address();
        address.setRecipientName(request.recipientName());
        address.setPhoneNumber(request.phoneNumber());
        address.setLine1(request.line1());
        address.setLine2(request.line2());
        address.setCity(request.city());
        address.setPostalCode(request.postalCode());
        address.setCountry(request.country());
        return address;
    }

    private AddressResponse toAddressResponse(Address address) {
        return new AddressResponse(
                address.getRecipientName(),
                address.getPhoneNumber(),
                address.getLine1(),
                address.getLine2(),
                address.getCity(),
                address.getPostalCode(),
                address.getCountry()
        );
    }

    private Money copyMoney(Money source) {
        return new Money(source.getAmount(), source.getCurrency());
    }
}