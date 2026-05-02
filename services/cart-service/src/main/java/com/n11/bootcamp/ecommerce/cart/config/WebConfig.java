package com.n11.bootcamp.ecommerce.cart.config;

import com.n11.bootcamp.ecommerce.security.feign.FeignSecurityConfig;
import com.n11.bootcamp.ecommerce.web.error.GlobalExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        GlobalExceptionHandler.class,
        FeignSecurityConfig.class
})
public class WebConfig {
}