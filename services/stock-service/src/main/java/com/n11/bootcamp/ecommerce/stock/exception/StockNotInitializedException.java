package com.n11.bootcamp.ecommerce.stock.exception;

import com.n11.bootcamp.ecommerce.web.exception.BusinessException;
import org.springframework.http.HttpStatus;


public class StockNotInitializedException extends BusinessException {

    public StockNotInitializedException(long productId) {
        super("STOCK_NOT_INITIALIZED", HttpStatus.NOT_FOUND,
                "Stock level not initialized for productId=" + productId);
    }
}