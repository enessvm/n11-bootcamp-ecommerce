package com.n11.bootcamp.ecommerce.payment.config;

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
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/payments/*/callback").permitAll()
                        .anyRequest().authenticated())
                .build();
    }
}