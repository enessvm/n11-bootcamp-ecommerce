package com.n11.bootcamp.ecommerce.product.exception;

import com.n11.bootcamp.ecommerce.web.exception.BusinessException;
import org.springframework.http.HttpStatus;


public class InvalidQueryParamException extends BusinessException {

    public InvalidQueryParamException(String message) {
        super("INVALID_QUERY_PARAM", HttpStatus.BAD_REQUEST, message);
    }
}