package com.n11.bootcamp.ecommerce.user.mapper;

import com.n11.bootcamp.ecommerce.user.dto.UserProfileResponse;
import com.n11.bootcamp.ecommerce.user.entity.UserProfile;
import org.springframework.stereotype.Component;

@Component
public class UserProfileMapper {

    public UserProfileResponse toResponse(UserProfile profile) {
        return new UserProfileResponse(
                profile.getKeycloakSub(),
                profile.getDisplayName(),
                profile.getPhoneNumber(),
                profile.getCreatedAt(),
                profile.getUpdatedAt()
        );
    }
}