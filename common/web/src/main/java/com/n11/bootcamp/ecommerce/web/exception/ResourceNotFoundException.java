package com.n11.bootcamp.ecommerce.web.exception;

import org.springframework.http.HttpStatus;

import java.util.Locale;

public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String resourceName, Object id) {
        super(
                buildErrorCode(resourceName),
                HttpStatus.NOT_FOUND,
                "%s with id %s not found".formatted(resourceName, id)
        );
    }

    private static String buildErrorCode(String resourceName) {
        return resourceName.toUpperCase(Locale.ROOT) + "_NOT_FOUND";
    }
}