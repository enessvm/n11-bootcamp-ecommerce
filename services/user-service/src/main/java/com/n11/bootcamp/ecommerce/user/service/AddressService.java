package com.n11.bootcamp.ecommerce.user.service;

import com.n11.bootcamp.ecommerce.user.dto.AddressResponse;
import com.n11.bootcamp.ecommerce.user.dto.CreateAddressRequest;
import com.n11.bootcamp.ecommerce.user.dto.UpdateAddressRequest;

import java.util.List;
import java.util.UUID;

public interface AddressService {

    List<AddressResponse> listForUser(UUID keycloakSub);

    AddressResponse create(UUID keycloakSub, CreateAddressRequest request);

    AddressResponse update(UUID keycloakSub, Long addressId, UpdateAddressRequest request);

    void delete(UUID keycloakSub, Long addressId);
}