package com.n11.bootcamp.ecommerce.user.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;


@ConfigurationProperties(prefix = "keycloak")
@Validated
@Getter
@Setter
public class KeycloakProperties {

    @NotBlank
    private String publicClientId;

    @Valid
    private Admin admin = new Admin();


    @Getter
    @Setter
    @Validated
    public static class Admin {

        @NotBlank
        private String serverUrl;

        @NotBlank
        private String realm;

        @NotBlank
        private String clientId;

        @NotBlank
        private String clientSecret;
    }
}
