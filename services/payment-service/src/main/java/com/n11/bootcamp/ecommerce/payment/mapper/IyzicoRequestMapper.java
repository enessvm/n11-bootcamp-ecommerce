package com.n11.bootcamp.ecommerce.payment.mapper;

import com.iyzipay.model.Address;
import com.iyzipay.model.BasketItem;
import com.iyzipay.model.BasketItemType;
import com.iyzipay.model.Buyer;
import com.iyzipay.model.Currency;
import com.iyzipay.model.Locale;
import com.iyzipay.model.PaymentGroup;
import com.iyzipay.request.CreateCheckoutFormInitializeRequest;
import com.n11.bootcamp.ecommerce.events.payment.ChargePaymentCommand;
import org.springframework.stereotype.Component;

/**
 * Translates between platform types ({@link ChargePaymentCommand}) and
 * Iyzico SDK types. Keeps Iyzico-specific knowledge isolated.
 */
@Component
public class IyzicoRequestMapper {

    public CreateCheckoutFormInitializeRequest toInitializeRequest(
            ChargePaymentCommand command,
            String callbackUrl
    ) {
        var request = new CreateCheckoutFormInitializeRequest();

        // Conversation correlation
        request.setLocale(Locale.TR.getValue());
        request.setConversationId(command.sagaId().toString());

        // Money
        request.setPrice(command.amount());
        request.setPaidPrice(command.paidAmount());
        request.setCurrency(mapCurrency(command.currency()));

        // Hosted-checkout config
        request.setBasketId(String.valueOf(command.orderId()));
        request.setPaymentGroup(PaymentGroup.PRODUCT.name());
        request.setCallbackUrl(callbackUrl);

        // Customer + addresses
        request.setBuyer(toBuyer(command));
        request.setShippingAddress(toAddress(command.shippingAddress()));
        request.setBillingAddress(toAddress(command.billingAddress()));

        // Line items already expanded by quantity (slice 1d-ii responsibility)
        request.setBasketItems(command.lineItems().stream()
                .map(this::toBasketItem)
                .toList());

        return request;
    }

    private Buyer toBuyer(ChargePaymentCommand command) {
        var customer = command.customer();
        var billing = command.billingAddress();

        var buyer = new Buyer();
        buyer.setId(customer.id());
        buyer.setName(customer.name());
        buyer.setSurname(customer.surname());
        buyer.setEmail(customer.email());
        buyer.setGsmNumber(customer.phoneNumber());
        buyer.setIdentityNumber(customer.identityNumber());
        buyer.setIp(customer.ipAddress());
        buyer.setRegistrationAddress(billing.addressLine());
        buyer.setCity(billing.city());
        buyer.setCountry(billing.country());
        buyer.setZipCode(billing.zipCode());

        return buyer;
    }

    private Address toAddress(ChargePaymentCommand.Address source) {
        var address = new Address();
        address.setContactName(source.contactName());
        address.setAddress(source.addressLine());
        address.setCity(source.city());
        address.setCountry(source.country());
        address.setZipCode(source.zipCode());
        return address;
    }

    private BasketItem toBasketItem(ChargePaymentCommand.LineItem source) {
        var item = new BasketItem();
        item.setId(source.id());
        item.setName(source.name());
        item.setCategory1(source.category());
        item.setItemType(BasketItemType.PHYSICAL.name());
        item.setPrice(source.price());
        return item;
    }

    private String mapCurrency(String code) {
        return switch (code) {
            case "TRY" -> Currency.TRY.name();
            case "USD" -> Currency.USD.name();
            case "EUR" -> Currency.EUR.name();
            case "GBP" -> Currency.GBP.name();
            default -> throw new IllegalArgumentException("Unsupported currency: " + code);
        };
    }
}