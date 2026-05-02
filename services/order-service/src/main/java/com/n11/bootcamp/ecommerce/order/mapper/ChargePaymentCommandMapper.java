package com.n11.bootcamp.ecommerce.order.mapper;

import com.n11.bootcamp.ecommerce.events.payment.ChargePaymentCommand;
import com.n11.bootcamp.ecommerce.order.entity.Address;
import com.n11.bootcamp.ecommerce.order.entity.Buyer;
import com.n11.bootcamp.ecommerce.order.entity.Order;
import com.n11.bootcamp.ecommerce.order.entity.OrderLineItem;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Translates an Order into the provider-neutral {@link ChargePaymentCommand}
 * event. Single seam between order-service's domain entities and the public
 * payment event contract. 
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
                toLineItems(order.getLineItems())
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

    private List<ChargePaymentCommand.LineItem> toLineItems(List<OrderLineItem> lineItems) {
        return lineItems.stream()
                .map(line -> new ChargePaymentCommand.LineItem(
                        String.valueOf(line.getProductId()),
                        line.getProductName(),
                        line.getCategoryName(),
                        line.getUnitEffectivePrice().getAmount(),
                        line.getQuantity()
                ))
                .toList();
    }
}
