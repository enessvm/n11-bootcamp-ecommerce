package com.n11.bootcamp.ecommerce.user.service;

import com.n11.bootcamp.ecommerce.user.dto.UpdateUserProfileRequest;
import com.n11.bootcamp.ecommerce.user.dto.UserProfileResponse;

import java.util.UUID;

public interface UserService {

    /**
     * Returns the profile for the given Keycloak {@code sub}, lazily creating
     * an empty row on first call.
     */
    UserProfileResponse getOrCreateProfile(UUID keycloakSub);

    UserProfileResponse updateMe(UUID keycloakSub, UpdateUserProfileRequest request);
}