package com.n11.bootcamp.ecommerce.payment.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;


@ConfigurationProperties(prefix = "iyzico")
@Validated
@Getter
@Setter
public class IyzicoProperties {

    @NotBlank
    private String apiKey;

    @NotBlank
    private String secretKey;

    @NotBlank
    private String baseUrl;

    @NotBlank
    private String callbackUrl;
}