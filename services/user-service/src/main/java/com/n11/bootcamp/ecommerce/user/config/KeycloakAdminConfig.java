package com.n11.bootcamp.ecommerce.user.config;

import lombok.RequiredArgsConstructor;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(KeycloakProperties.class)
public class KeycloakAdminConfig {

    private final KeycloakProperties properties;

    @Bean
    public Keycloak adminKeycloak() {
        KeycloakProperties.Admin admin = properties.getAdmin();
        return KeycloakBuilder.builder()
                .serverUrl(admin.getServerUrl())
                .realm(admin.getRealm())
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .clientId(admin.getClientId())
                .clientSecret(admin.getClientSecret())
                .build();
    }
}
