package com.n11.bootcamp.ecommerce.product.config;

import com.n11.bootcamp.ecommerce.web.error.GlobalExceptionHandler;
import com.n11.bootcamp.ecommerce.security.feign.FeignSecurityConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        GlobalExceptionHandler.class,
        FeignSecurityConfig.class
})
public class WebConfig {
}