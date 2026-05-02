package com.n11.bootcamp.ecommerce.order.exception;

import com.n11.bootcamp.ecommerce.web.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class ProfileIncompleteException extends BusinessException {

    public ProfileIncompleteException(String missingField) {
        super("PROFILE_INCOMPLETE", HttpStatus.BAD_REQUEST,
                "Profile is incomplete: " + missingField + " is required to place an order");
    }
}