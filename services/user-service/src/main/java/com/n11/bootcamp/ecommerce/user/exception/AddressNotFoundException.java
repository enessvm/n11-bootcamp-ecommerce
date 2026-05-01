package com.n11.bootcamp.ecommerce.user.exception;

import com.n11.bootcamp.ecommerce.web.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class AddressNotFoundException extends BusinessException {

    public AddressNotFoundException(Long id) {
        super("ADDRESS_NOT_FOUND", HttpStatus.NOT_FOUND, "Address with id " + id + " not found");
    }
}