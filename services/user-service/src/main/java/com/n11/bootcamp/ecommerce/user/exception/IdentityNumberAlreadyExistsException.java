package com.n11.bootcamp.ecommerce.user.exception;

import com.n11.bootcamp.ecommerce.web.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class IdentityNumberAlreadyExistsException extends BusinessException {

    public IdentityNumberAlreadyExistsException() {
        super("IDENTITY_NUMBER_ALREADY_EXISTS", HttpStatus.CONFLICT,
                "This identity number is already registered");
    }
}
