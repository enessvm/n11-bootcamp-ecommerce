package com.n11.bootcamp.ecommerce.user.auth.keycloak;

import com.n11.bootcamp.ecommerce.user.config.KeycloakProperties;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Slf4j
@Component
@RequiredArgsConstructor
public class KeycloakAdminClient {

    private final Keycloak adminClient;
    private final KeycloakProperties properties;

    /**
     * Creates a Keycloak user in the application realm.
     * Returns the Keycloak {@code sub} (UUID) on success.
     */
    public Optional<UUID> createUser(String email,
                                     String password,
                                     String firstName,
                                     String lastName) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(email);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEnabled(true);
        user.setEmailVerified(true);

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        credential.setTemporary(false);
        user.setCredentials(List.of(credential));

        try (Response response = realm().users().create(user)) {
            int status = response.getStatus();
            if (status == Response.Status.CREATED.getStatusCode()) {
                UUID sub = extractIdFromLocation(response.getHeaderString(HttpHeaders.LOCATION));
                log.info("Created Keycloak user email={} sub={}", email, sub);
                return Optional.of(sub);
            }
            if (status == Response.Status.CONFLICT.getStatusCode()) {
                log.info("Keycloak user already exists email={}", email);
                return Optional.empty();
            }
            String body = response.hasEntity() ? response.readEntity(String.class) : "<empty>";
            log.warn("Keycloak createUser failed email={} status={} body={}", email, status, body);
            throw new IllegalStateException(
                    "Keycloak createUser failed with status " + status);
        }
    }

    /**
     * Best-effort delete used as compensation when post-create work fails.
     */
    public void deleteUser(UUID keycloakSub) {
        try {
            realm().users().delete(keycloakSub.toString());
            log.info("Deleted Keycloak user sub={}", keycloakSub);
        } catch (Exception e) {
            log.warn("Failed to compensate-delete Keycloak user sub={}: {}",
                    keycloakSub, e.getMessage());
        }
    }

    private RealmResource realm() {
        return adminClient.realm(properties.getAdmin().getRealm());
    }

    private static UUID extractIdFromLocation(String location) {
        if (location == null || location.isBlank()) {
            throw new IllegalStateException(
                    "Keycloak createUser response missing Location header");
        }
        String id = location.substring(location.lastIndexOf('/') + 1);
        return UUID.fromString(id);
    }
}
