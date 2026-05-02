package com.n11.bootcamp.ecommerce.cart.service;

import com.n11.bootcamp.ecommerce.cart.dto.AddCartItemRequest;
import com.n11.bootcamp.ecommerce.cart.dto.CartResponse;
import com.n11.bootcamp.ecommerce.cart.dto.UpdateCartItemRequest;

import java.util.UUID;

public interface CartService {

    CartResponse getOrCreateCart(UUID keycloakSub);


    CartResponse addItem(UUID keycloakSub, AddCartItemRequest request);


    CartResponse updateItem(UUID keycloakSub, long productId, UpdateCartItemRequest request);


    CartResponse removeItem(UUID keycloakSub, long productId);


    CartResponse clearCart(UUID keycloakSub);
}