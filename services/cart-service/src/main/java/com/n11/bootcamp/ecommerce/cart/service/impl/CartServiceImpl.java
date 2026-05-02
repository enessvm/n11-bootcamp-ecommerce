package com.n11.bootcamp.ecommerce.cart.service.impl;

import com.n11.bootcamp.ecommerce.cart.client.ProductServiceClient;
import com.n11.bootcamp.ecommerce.cart.client.dto.ProductBatchRequest;
import com.n11.bootcamp.ecommerce.cart.client.dto.ProductBatchResponse;
import com.n11.bootcamp.ecommerce.cart.dto.AddCartItemRequest;
import com.n11.bootcamp.ecommerce.cart.dto.CartResponse;
import com.n11.bootcamp.ecommerce.cart.dto.UpdateCartItemRequest;
import com.n11.bootcamp.ecommerce.cart.entity.Cart;
import com.n11.bootcamp.ecommerce.cart.entity.CartItem;
import com.n11.bootcamp.ecommerce.cart.exception.CartItemNotFoundException;
import com.n11.bootcamp.ecommerce.cart.mapper.CartMapper;
import com.n11.bootcamp.ecommerce.cart.repository.CartRepository;
import com.n11.bootcamp.ecommerce.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ProductServiceClient productServiceClient;
    private final CartMapper cartMapper;

    @Override
    @Transactional
    public CartResponse getOrCreateCart(UUID keycloakSub) {
        Cart cart = loadOrCreate(keycloakSub);
        return enrichAndMap(cart);
    }

    @Override
    @Transactional
    public CartResponse addItem(UUID keycloakSub, AddCartItemRequest request) {
        Cart cart = loadOrCreate(keycloakSub);

        Optional<CartItem> existing = findItemByProductId(cart, request.productId());

        if (existing.isPresent()) {
            CartItem item = existing.get();
            int newQuantity = item.getQuantity() + request.quantity();
            item.setQuantity(newQuantity);
            log.info("Merged cartItemId={} productId={} newQuantity={}",
                    item.getId(), request.productId(), newQuantity);
        } else {
            CartItem newItem = CartItem.create(cart, request.productId(), request.quantity());
            cart.getLineItems().add(newItem);
            log.info("Added new line productId={} quantity={} to cartId={}",
                    request.productId(), request.quantity(), cart.getId());
        }

        return enrichAndMap(cart);
    }

    @Override
    @Transactional
    public CartResponse updateItem(UUID keycloakSub, long productId, UpdateCartItemRequest request) {
        Cart cart = loadOrCreate(keycloakSub);

        CartItem item = findItemByProductId(cart, productId)
                .orElseThrow(() -> new CartItemNotFoundException(productId));

        item.setQuantity(request.quantity());
        log.info("Updated cartItemId={} productId={} quantity={}",
                item.getId(), productId, request.quantity());

        return enrichAndMap(cart);
    }

    @Override
    @Transactional
    public CartResponse removeItem(UUID keycloakSub, long productId) {
        Cart cart = loadOrCreate(keycloakSub);

        boolean removed = cart.getLineItems().removeIf(item -> item.getProductId() == productId);

        if (removed) {
            log.info("Removed productId={} from cartId={}", productId, cart.getId());
        } else {
            log.info("Remove no-op productId={} not in cartId={}", productId, cart.getId());
        }

        return enrichAndMap(cart);
    }

    @Override
    @Transactional
    public CartResponse clearCart(UUID keycloakSub) {
        Cart cart = loadOrCreate(keycloakSub);
        int sizeBefore = cart.getLineItems().size();

        // deletes all cart_item rows on flush. The cart row stays.
        cart.getLineItems().clear();

        log.info("Cleared cartId={} ({} items removed)", cart.getId(), sizeBefore);
        return enrichAndMap(cart);
    }

    // ---- Helpers ----

    private Cart loadOrCreate(UUID keycloakSub) {
        return cartRepository.findByKeycloakSub(keycloakSub)
                .orElseGet(() -> {
                    log.info("Lazy-creating cart for keycloakSub={}", keycloakSub);
                    return cartRepository.save(Cart.create(keycloakSub));
                });
    }

    private Optional<CartItem> findItemByProductId(Cart cart, long productId) {
        return cart.getLineItems().stream()
                .filter(item -> item.getProductId() == productId)
                .findFirst();
    }

    private CartResponse enrichAndMap(Cart cart) {
        Map<Long, ProductBatchResponse.BatchEntry> productsById = fetchProducts(cart);
        return cartMapper.toResponse(cart, productsById);
    }

    private Map<Long, ProductBatchResponse.BatchEntry> fetchProducts(Cart cart) {
        if (cart.getLineItems().isEmpty()) {
            return Map.of();
        }

        List<Long> productIds = cart.getLineItems().stream()
                .map(CartItem::getProductId)
                .distinct()
                .toList();

        ProductBatchResponse response = productServiceClient.batchProducts(
                new ProductBatchRequest(productIds));

        return response.products().stream()
                .collect(Collectors.toMap(
                        ProductBatchResponse.BatchEntry::id,
                        Function.identity()
                ));
    }
}