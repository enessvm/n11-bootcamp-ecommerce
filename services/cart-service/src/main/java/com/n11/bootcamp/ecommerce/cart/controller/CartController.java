package com.n11.bootcamp.ecommerce.cart.controller;

import com.n11.bootcamp.ecommerce.cart.dto.AddCartItemRequest;
import com.n11.bootcamp.ecommerce.cart.dto.CartResponse;
import com.n11.bootcamp.ecommerce.cart.dto.UpdateCartItemRequest;
import com.n11.bootcamp.ecommerce.cart.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/cart")
    public CartResponse getCart(@AuthenticationPrincipal Jwt jwt) {
        return cartService.getOrCreateCart(sub(jwt));
    }

    @PostMapping("/cart/items")
    public CartResponse addItem(@AuthenticationPrincipal Jwt jwt,
                                @Valid @RequestBody AddCartItemRequest request) {
        return cartService.addItem(sub(jwt), request);
    }

    @PutMapping("/cart/items/{productId}")
    public CartResponse updateItem(@AuthenticationPrincipal Jwt jwt,
                                   @PathVariable long productId,
                                   @Valid @RequestBody UpdateCartItemRequest request) {
        return cartService.updateItem(sub(jwt), productId, request);
    }

    @DeleteMapping("/cart/items/{productId}")
    public CartResponse removeItem(@AuthenticationPrincipal Jwt jwt,
                                   @PathVariable long productId) {
        return cartService.removeItem(sub(jwt), productId);
    }

    @DeleteMapping("/cart")
    public CartResponse clearCart(@AuthenticationPrincipal Jwt jwt) {
        return cartService.clearCart(sub(jwt));
    }

    private UUID sub(Jwt jwt) {
        return UUID.fromString(jwt.getSubject());
    }
}