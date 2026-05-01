package com.n11.bootcamp.ecommerce.order.controller;

import com.n11.bootcamp.ecommerce.order.dto.CreateOrderRequest;
import com.n11.bootcamp.ecommerce.order.dto.OrderResponse;
import com.n11.bootcamp.ecommerce.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;


    @PostMapping("/orders")
    public ResponseEntity<OrderResponse> create(@AuthenticationPrincipal Jwt jwt,
                                                @Valid @RequestBody CreateOrderRequest request) {
        OrderResponse created = orderService.createOrder(sub(jwt), request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.status(HttpStatus.ACCEPTED).location(location).body(created);
    }

    @GetMapping("/orders/{id}")
    public OrderResponse getById(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        return orderService.getById(id, sub(jwt));
    }

    private UUID sub(Jwt jwt) {
        return UUID.fromString(jwt.getSubject());
    }
}