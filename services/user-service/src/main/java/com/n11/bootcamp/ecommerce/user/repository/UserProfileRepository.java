package com.n11.bootcamp.ecommerce.user.repository;

import com.n11.bootcamp.ecommerce.user.entity.UserProfile;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.UUID;

public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {


    @Modifying
    @Transactional
    @Query(value = """
        INSERT INTO user_profile (keycloak_sub, display_name, phone_number, created_at, updated_at)
        VALUES (:sub, NULL, NULL, :now, :now)
        ON CONFLICT (keycloak_sub) DO NOTHING
        """, nativeQuery = true)
    int insertIfAbsent(@Param("sub") UUID keycloakSub, @Param("now") Instant now);
}