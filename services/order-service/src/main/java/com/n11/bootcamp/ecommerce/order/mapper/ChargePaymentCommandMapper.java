package com.n11.bootcamp.ecommerce.order.mapper;

import com.n11.bootcamp.ecommerce.events.payment.ChargePaymentCommand;
import com.n11.bootcamp.ecommerce.order.entity.Address;
import com.n11.bootcamp.ecommerce.order.entity.Buyer;
import com.n11.bootcamp.ecommerce.order.entity.Order;
import com.n11.bootcamp.ecommerce.order.entity.OrderLineItem;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Translates an Order into the Iyzico-shaped ChargePaymentCommand.
 *
 * <p>Single seam between order schema and payment schema. LineItems are
 * expanded by quantity (3 of product X = 3 LineItems) because Iyzico's
 * BasketItem has no quantity field.
 */
@Component
public class ChargePaymentCommandMapper {

    public ChargePaymentCommand toCommand(Order order) {
        return new ChargePaymentCommand(
                UUID.randomUUID(),
                Instant.now(),
                order.getSagaId(),
                order.getId(),
                order.getPaymentProvider(),
                order.getSubtotal().getAmount(),
                order.getTotal().getAmount(),
                order.getTotal().getCurrency(),
                toCustomer(order),
                toCommandAddress(order.getShippingAddress()),
                toCommandAddress(order.getBillingAddress()),
                expandLineItems(order.getLineItems())
        );
    }

    private ChargePaymentCommand.Customer toCustomer(Order order) {
        Buyer buyer = order.getBuyer();
        return new ChargePaymentCommand.Customer(
                order.getUserId().toString(),
                buyer.getFirstName(),
                buyer.getLastName(),
                buyer.getEmail(),
                buyer.getPhoneNumber(),
                buyer.getIdentityNumber(),
                buyer.getIpAddress()
        );
    }

    private ChargePaymentCommand.Address toCommandAddress(Address address) {
        return new ChargePaymentCommand.Address(
                address.getRecipientName(),
                joinAddressLines(address.getLine1(), address.getLine2()),
                address.getCity(),
                address.getCountry(),
                address.getPostalCode()
        );
    }

    private String joinAddressLines(String line1, String line2) {
        if (line2 == null || line2.isBlank()) {
            return line1;
        }
        return line1 + ", " + line2;
    }

    private List<ChargePaymentCommand.LineItem> expandLineItems(List<OrderLineItem> lineItems) {
        List<ChargePaymentCommand.LineItem> expanded = new ArrayList<>();
        for (OrderLineItem line : lineItems) {
            String itemId = String.valueOf(line.getProductId());
            for (int i = 0; i < line.getQuantity(); i++) {
                expanded.add(new ChargePaymentCommand.LineItem(
                        itemId,
                        line.getProductName(),
                        line.getCategoryName(),
                        line.getUnitEffectivePrice().getAmount()
                ));
            }
        }
        return expanded;
    }
}
