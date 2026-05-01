package com.n11.bootcamp.ecommerce.user.service.impl;

import com.n11.bootcamp.ecommerce.user.dto.AddressResponse;
import com.n11.bootcamp.ecommerce.user.dto.CreateAddressRequest;
import com.n11.bootcamp.ecommerce.user.dto.UpdateAddressRequest;
import com.n11.bootcamp.ecommerce.user.entity.Address;
import com.n11.bootcamp.ecommerce.user.exception.AddressNotFoundException;
import com.n11.bootcamp.ecommerce.user.mapper.AddressMapper;
import com.n11.bootcamp.ecommerce.user.repository.AddressRepository;
import com.n11.bootcamp.ecommerce.user.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;

    @Override
    @Transactional(readOnly = true)
    public List<AddressResponse> listForUser(UUID keycloakSub) {
        return addressRepository.findAllByKeycloakSub(keycloakSub).stream()
                .map(addressMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public AddressResponse create(UUID keycloakSub, CreateAddressRequest request) {
        Address address = addressMapper.toEntity(request, keycloakSub);
        Address saved = addressRepository.save(address);

        if (saved.isDefault()) {
            addressRepository.clearDefaultsExcept(keycloakSub, saved.getId());
        }

        return addressMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public AddressResponse update(UUID keycloakSub, Long addressId, UpdateAddressRequest request) {
        Address existing = addressRepository.findByIdAndKeycloakSub(addressId, keycloakSub)
                .orElseThrow(() -> new AddressNotFoundException(addressId));

        addressMapper.updateEntity(existing, request);

        if (existing.isDefault()) {
            addressRepository.clearDefaultsExcept(keycloakSub, existing.getId());
        }

        return addressMapper.toResponse(existing);
    }

    @Override
    @Transactional
    public void delete(UUID keycloakSub, Long addressId) {
        Address existing = addressRepository.findByIdAndKeycloakSub(addressId, keycloakSub)
                .orElseThrow(() -> new AddressNotFoundException(addressId));

        addressRepository.delete(existing);
    }
}