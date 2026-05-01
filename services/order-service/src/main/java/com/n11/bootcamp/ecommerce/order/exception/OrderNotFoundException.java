package com.n11.bootcamp.ecommerce.order.exception;

import com.n11.bootcamp.ecommerce.web.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class OrderNotFoundException extends BusinessException {

    public OrderNotFoundException(Long id) {
        super("ORDER_NOT_FOUND", HttpStatus.NOT_FOUND,
                "Order with id " + id + " not found");
    }
}