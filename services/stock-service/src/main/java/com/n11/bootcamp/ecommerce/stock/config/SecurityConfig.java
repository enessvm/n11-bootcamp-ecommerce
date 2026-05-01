package com.n11.bootcamp.ecommerce.stock.config;

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

                        // Internal Feign endpoint
                        .requestMatchers(HttpMethod.POST, "/stock/batch").permitAll()

                        // Admin writes
                        .requestMatchers(HttpMethod.PUT,  "/stock/**").hasRole("ADMIN")

                        // Anything else
                        .anyRequest().authenticated())
                .build();
    }
}