package com.n11.bootcamp.ecommerce.promotion.config;

import com.n11.bootcamp.ecommerce.web.error.GlobalExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(GlobalExceptionHandler.class)
public class WebConfig {
}