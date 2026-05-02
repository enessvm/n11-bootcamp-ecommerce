package com.n11.bootcamp.ecommerce.user.auth.keycloak;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.n11.bootcamp.ecommerce.user.config.KeycloakProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.Optional;


@Slf4j
@Component
public class KeycloakTokenClient {

    private static final String TOKEN_PATH = "/protocol/openid-connect/token";
    private static final String LOGOUT_PATH = "/protocol/openid-connect/logout";

    private final KeycloakProperties properties;
    private final RestClient restClient;

    public KeycloakTokenClient(KeycloakProperties properties,
                               RestClient.Builder builder) {
        this.properties = properties;
        String baseUrl = properties.getAdmin().getServerUrl()
                + "/realms/" + properties.getAdmin().getRealm();
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    /**
     * ROPC exchange against the public client. Returns tokens on success.
     * Returns {@link Optional#empty()} when Keycloak responds 401 (bad credentials).
     * Throws on any other failure.
     */
    public Optional<KeycloakTokens> passwordGrant(String email, String password) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "password");
        form.add("client_id", properties.getPublicClientId());
        form.add("username", email);
        form.add("password", password);
        form.add("scope", "openid profile email");

        return postForTokens(form, "passwordGrant email=" + email);
    }

    /**
     * Refresh exchange. Returns new tokens on success.
     * Returns {@link Optional#empty()} when Keycloak responds 400/401
     * (refresh token expired, revoked, or invalid).
     */
    public Optional<KeycloakTokens> refresh(String refreshToken) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "refresh_token");
        form.add("client_id", properties.getPublicClientId());
        form.add("refresh_token", refreshToken);

        return postForTokens(form, "refresh");
    }

    /**
     * Best-effort revocation of the refresh token (Keycloak end-session).
     * Swallows errors so logout always appears successful to the caller.
     */
    public void logout(String refreshToken) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("client_id", properties.getPublicClientId());
        form.add("refresh_token", refreshToken);

        try {
            restClient.post()
                    .uri(LOGOUT_PATH)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(form)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.warn("Keycloak logout failed (best-effort): {}", e.getMessage());
        }
    }

    private Optional<KeycloakTokens> postForTokens(MultiValueMap<String, String> form,
                                                   String operation) {
        try {
            KeycloakTokens tokens = restClient.post()
                    .uri(TOKEN_PATH)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(form)
                    .retrieve()
                    .body(KeycloakTokens.class);
            return Optional.ofNullable(tokens);
        } catch (HttpClientErrorException.Unauthorized | HttpClientErrorException.BadRequest e) {
            log.info("Keycloak {} rejected: status={}", operation, e.getStatusCode());
            return Optional.empty();
        }
    }


    public record KeycloakTokens(
            @JsonProperty("access_token") String accessToken,
            @JsonProperty("refresh_token") String refreshToken,
            @JsonProperty("expires_in") long expiresIn,
            @JsonProperty("refresh_expires_in") long refreshExpiresIn,
            @JsonProperty("token_type") String tokenType
    ) {}
}
