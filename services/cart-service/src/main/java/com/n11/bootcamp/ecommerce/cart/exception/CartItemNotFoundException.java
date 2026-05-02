package com.n11.bootcamp.ecommerce.cart.exception;

import com.n11.bootcamp.ecommerce.web.exception.BusinessException;
import org.springframework.http.HttpStatus;


public class CartItemNotFoundException extends BusinessException {

    public CartItemNotFoundException(long productId) {
        super("CART_ITEM_NOT_FOUND", HttpStatus.NOT_FOUND,
                "No item with productId=" + productId + " in cart");
    }
}