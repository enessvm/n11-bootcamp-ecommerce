package com.n11.bootcamp.ecommerce.security.feign;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class FeignSecurityConfig {

    @Bean
    public JwtRequestInterceptor jwtPropagatingRequestInterceptor() {
        return new JwtRequestInterceptor();
    }
}