package com.n11.bootcamp.ecommerce.user.exception;

import com.n11.bootcamp.ecommerce.web.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class EmailAlreadyExistsException extends BusinessException {

    public EmailAlreadyExistsException(String email) {
        super("EMAIL_ALREADY_EXISTS", HttpStatus.CONFLICT,
                "An account with email " + email + " already exists");
    }
}
