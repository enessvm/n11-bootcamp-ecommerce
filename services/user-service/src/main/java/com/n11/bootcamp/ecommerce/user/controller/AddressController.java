package com.n11.bootcamp.ecommerce.user.controller;

import com.n11.bootcamp.ecommerce.user.dto.AddressResponse;
import com.n11.bootcamp.ecommerce.user.dto.CreateAddressRequest;
import com.n11.bootcamp.ecommerce.user.dto.UpdateAddressRequest;
import com.n11.bootcamp.ecommerce.user.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @GetMapping("/users/me/addresses")
    public List<AddressResponse> list(@AuthenticationPrincipal Jwt jwt) {
        return addressService.listForUser(sub(jwt));
    }

    @PostMapping("/users/me/addresses")
    public ResponseEntity<AddressResponse> create(@AuthenticationPrincipal Jwt jwt,
                                                  @Valid @RequestBody CreateAddressRequest request) {
        AddressResponse created = addressService.create(sub(jwt), request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/users/me/addresses/{id}")
    public AddressResponse update(@AuthenticationPrincipal Jwt jwt,
                                  @PathVariable Long id,
                                  @Valid @RequestBody UpdateAddressRequest request) {
        return addressService.update(sub(jwt), id, request);
    }

    @DeleteMapping("/users/me/addresses/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        addressService.delete(sub(jwt), id);
    }

    private UUID sub(Jwt jwt) {
        return UUID.fromString(jwt.getSubject());
    }
}