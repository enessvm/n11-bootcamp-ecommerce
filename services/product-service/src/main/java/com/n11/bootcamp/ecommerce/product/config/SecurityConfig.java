package com.n11.bootcamp.ecommerce.product.config;

import com.n11.bootcamp.ecommerce.security.SecurityDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return SecurityDefaults.applyDefaults(http)
                .authorizeHttpRequests(auth -> auth
                        // Operational
                        .requestMatchers("/actuator/**").permitAll()

                        // Internal Feign — must be ahead of /products write rules
                        .requestMatchers(HttpMethod.POST, "/products/batch").authenticated()

                        // Public reads
                        .requestMatchers(HttpMethod.GET, "/categories").permitAll()
                        .requestMatchers(HttpMethod.GET, "/products", "/products/*").permitAll()

                        // Admin writes
                        .requestMatchers(HttpMethod.POST, "/products").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/products/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/products/*").hasRole("ADMIN")

                        // Anything else
                        .anyRequest().authenticated())
                .build();
    }
}