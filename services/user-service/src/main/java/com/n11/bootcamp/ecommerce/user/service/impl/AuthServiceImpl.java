package com.n11.bootcamp.ecommerce.user.service.impl;

import com.n11.bootcamp.ecommerce.user.auth.keycloak.KeycloakAdminClient;
import com.n11.bootcamp.ecommerce.user.auth.keycloak.KeycloakTokenClient;
import com.n11.bootcamp.ecommerce.user.auth.keycloak.KeycloakTokenClient.KeycloakTokens;
import com.n11.bootcamp.ecommerce.user.dto.AuthResponse;
import com.n11.bootcamp.ecommerce.user.dto.LoginRequest;
import com.n11.bootcamp.ecommerce.user.dto.RefreshRequest;
import com.n11.bootcamp.ecommerce.user.dto.RegisterRequest;
import com.n11.bootcamp.ecommerce.user.entity.UserProfile;
import com.n11.bootcamp.ecommerce.user.exception.EmailAlreadyExistsException;
import com.n11.bootcamp.ecommerce.user.exception.IdentityNumberAlreadyExistsException;
import com.n11.bootcamp.ecommerce.user.exception.InvalidCredentialsException;
import com.n11.bootcamp.ecommerce.user.mapper.UserProfileMapper;
import com.n11.bootcamp.ecommerce.user.repository.UserProfileRepository;
import com.n11.bootcamp.ecommerce.user.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final KeycloakAdminClient adminClient;
    private final KeycloakTokenClient tokenClient;
    private final UserProfileRepository userProfileRepository;
    private final UserProfileMapper userProfileMapper;
    private final JwtDecoder jwtDecoder;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userProfileRepository.existsByIdentityNumber(request.identityNumber())) {
            throw new IdentityNumberAlreadyExistsException();
        }

        UUID keycloakSub = adminClient.createUser(
                        request.email(),
                        request.password(),
                        request.firstName(),
                        request.lastName())
                .orElseThrow(() -> new EmailAlreadyExistsException(request.email()));

        UserProfile profile;
        try {
            profile = persistProfile(request, keycloakSub);
        } catch (RuntimeException e) {
            log.warn("Profile insert failed for sub={}; compensating Keycloak user.", keycloakSub);
            adminClient.deleteUser(keycloakSub);
            throw e;
        }

        KeycloakTokens tokens = tokenClient.passwordGrant(request.email(), request.password())
                .orElseThrow(() -> new IllegalStateException(
                        "Token exchange failed immediately after registration"));

        return toResponse(tokens, profile);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        KeycloakTokens tokens = tokenClient.passwordGrant(request.email(), request.password())
                .orElseThrow(InvalidCredentialsException::new);

        UUID sub = decodeSub(tokens.accessToken());
        UserProfile profile = userProfileRepository.findById(sub).orElseGet(() -> {
            // Lazy create for pre-existing Keycloak users
            userProfileRepository.insertIfAbsent(sub, Instant.now());
            return userProfileRepository.findById(sub).orElseThrow();
        });

        return toResponse(tokens, profile);
    }

    @Override
    public AuthResponse refresh(RefreshRequest request) {
        KeycloakTokens tokens = tokenClient.refresh(request.refreshToken())
                .orElseThrow(InvalidCredentialsException::new);
        return toResponse(tokens, null);
    }

    @Override
    public void logout(String refreshToken) {
        if (refreshToken != null && !refreshToken.isBlank()) {
            tokenClient.logout(refreshToken);
        }
    }

    private UserProfile persistProfile(RegisterRequest request, UUID keycloakSub) {
        UserProfile profile = new UserProfile();
        profile.setKeycloakSub(keycloakSub);
        profile.setDisplayName(request.firstName() + " " + request.lastName());
        profile.setPhoneNumber(request.phoneNumber());
        profile.setIdentityNumber(request.identityNumber());
        return userProfileRepository.save(profile);
    }

    private UUID decodeSub(String accessToken) {
        Jwt jwt = jwtDecoder.decode(accessToken);
        return UUID.fromString(jwt.getSubject());
    }

    private AuthResponse toResponse(KeycloakTokens tokens, UserProfile profile) {
        return new AuthResponse(
                tokens.accessToken(),
                tokens.refreshToken(),
                tokens.expiresIn(),
                tokens.refreshExpiresIn(),
                tokens.tokenType(),
                profile == null ? null : userProfileMapper.toResponse(profile)
        );
    }
}
