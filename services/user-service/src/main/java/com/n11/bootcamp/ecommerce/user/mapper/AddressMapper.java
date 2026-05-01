package com.n11.bootcamp.ecommerce.user.mapper;

import com.n11.bootcamp.ecommerce.user.dto.AddressResponse;
import com.n11.bootcamp.ecommerce.user.dto.CreateAddressRequest;
import com.n11.bootcamp.ecommerce.user.dto.UpdateAddressRequest;
import com.n11.bootcamp.ecommerce.user.entity.Address;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AddressMapper {

    public Address toEntity(CreateAddressRequest request, UUID keycloakSub) {
        Address address = new Address();
        address.setKeycloakSub(keycloakSub);
        address.setLabel(request.label());
        address.setRecipientName(request.recipientName());
        address.setPhoneNumber(request.phoneNumber());
        address.setLine1(request.line1());
        address.setLine2(request.line2());
        address.setCity(request.city());
        address.setPostalCode(request.postalCode());
        address.setCountry(request.country());
        address.setDefault(request.isDefault());
        return address;
    }

    public void updateEntity(Address existing, UpdateAddressRequest request) {
        existing.setLabel(request.label());
        existing.setRecipientName(request.recipientName());
        existing.setPhoneNumber(request.phoneNumber());
        existing.setLine1(request.line1());
        existing.setLine2(request.line2());
        existing.setCity(request.city());
        existing.setPostalCode(request.postalCode());
        existing.setCountry(request.country());
        existing.setDefault(request.isDefault());
    }

    public AddressResponse toResponse(Address address) {
        return new AddressResponse(
                address.getId(),
                address.getLabel(),
                address.getRecipientName(),
                address.getPhoneNumber(),
                address.getLine1(),
                address.getLine2(),
                address.getCity(),
                address.getPostalCode(),
                address.getCountry(),
                address.isDefault(),
                address.getCreatedAt(),
                address.getUpdatedAt()
        );
    }
}