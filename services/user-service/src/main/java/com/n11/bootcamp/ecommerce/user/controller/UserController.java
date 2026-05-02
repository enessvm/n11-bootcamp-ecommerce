package com.n11.bootcamp.ecommerce.user.controller;

import com.n11.bootcamp.ecommerce.user.dto.UpdateUserProfileRequest;
import com.n11.bootcamp.ecommerce.user.dto.UserProfileResponse;
import com.n11.bootcamp.ecommerce.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/users/me")
    public UserProfileResponse getMe(@AuthenticationPrincipal Jwt jwt) {
        UUID keycloakSub = UUID.fromString(jwt.getSubject());
        return userService.getOrCreateProfile(keycloakSub);
    }


    @PutMapping("/users/me")
    public UserProfileResponse updateMe(@Valid @AuthenticationPrincipal Jwt jwt, @RequestBody UpdateUserProfileRequest request) {
        UUID keycloakSub = UUID.fromString(jwt.getSubject());
        return userService.updateMe(keycloakSub, request);
    }
}