package com.n11.bootcamp.ecommerce.user.repository;

import com.n11.bootcamp.ecommerce.user.entity.Address;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AddressRepository extends JpaRepository<Address, Long> {

    List<Address> findAllByKeycloakSub(UUID keycloakSub);

    /**
     * Ownership-checked lookup. Returns the address only if it belongs to the given user
     */
    Optional<Address> findByIdAndKeycloakSub(Long id, UUID keycloakSub);

    /**
     * Flip every other address for this user to non-default.
     */
    @Modifying
    @Transactional
    @Query("""
        UPDATE Address a
        SET a.isDefault = false
        WHERE a.keycloakSub = :sub
          AND a.id <> :excludeId
          AND a.isDefault = true
        """)
    int clearDefaultsExcept(@Param("sub") UUID keycloakSub, @Param("excludeId") Long excludeId);
}