package com.n11.bootcamp.ecommerce.payment.config;

import com.iyzipay.Options;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Builds the Iyzico SDK's {@link Options} bean from {@link IyzicoProperties}.
 * for every SDK call.
 */
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(IyzicoProperties.class)
public class IyzicoOptionsConfig {

    private final IyzicoProperties properties;

    @Bean
    public Options iyzicoOptions() {
        Options options = new Options();
        options.setApiKey(properties.getApiKey());
        options.setSecretKey(properties.getSecretKey());
        options.setBaseUrl(properties.getBaseUrl());
        return options;
    }
}