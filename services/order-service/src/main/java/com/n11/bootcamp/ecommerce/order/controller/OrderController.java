package com.n11.bootcamp.ecommerce.order.controller;

import com.n11.bootcamp.ecommerce.order.dto.CreateOrderRequest;
import com.n11.bootcamp.ecommerce.order.dto.OrderListResponse;
import com.n11.bootcamp.ecommerce.order.dto.OrderResponse;
import com.n11.bootcamp.ecommerce.order.service.OrderService;
import com.n11.bootcamp.ecommerce.order.util.ClientIpResolver;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Validated
public class OrderController {

    private final OrderService orderService;


    @PostMapping("/orders")
    public ResponseEntity<OrderResponse> create(@AuthenticationPrincipal Jwt jwt,
                                                HttpServletRequest httpRequest,
                                                @Valid @RequestBody CreateOrderRequest request) {
        String ipAddress = ClientIpResolver.resolve(httpRequest);
        OrderResponse created = orderService.createOrder(jwt, ipAddress, request);
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

    @GetMapping("/orders")
    public OrderListResponse listMyOrders(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(50) int size) {
        return orderService.listOrdersForUser(sub(jwt), page, size);
    }

    private UUID sub(Jwt jwt) {
        return UUID.fromString(jwt.getSubject());
    }
}
