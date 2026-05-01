package com.n11.bootcamp.ecommerce.user.service.impl;

import com.n11.bootcamp.ecommerce.user.dto.UserProfileResponse;
import com.n11.bootcamp.ecommerce.user.entity.UserProfile;
import com.n11.bootcamp.ecommerce.user.mapper.UserProfileMapper;
import com.n11.bootcamp.ecommerce.user.repository.UserProfileRepository;
import com.n11.bootcamp.ecommerce.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserProfileRepository userProfileRepository;
    private final UserProfileMapper userProfileMapper;

    @Override
    @Transactional
    public UserProfileResponse getOrCreateProfile(UUID keycloakSub) {
        userProfileRepository.insertIfAbsent(keycloakSub, Instant.now());
        UserProfile profile = userProfileRepository.findById(keycloakSub).orElseThrow();
        return userProfileMapper.toResponse(profile);
    }
}