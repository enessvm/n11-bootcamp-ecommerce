package com.n11.bootcamp.ecommerce.user.entity;

import com.n11.bootcamp.ecommerce.persistence.Auditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "user_profile")
@Getter
@Setter
@NoArgsConstructor()
public class UserProfile extends Auditable {

    @Id
    @Column(name = "keycloak_sub")
    private UUID keycloakSub;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "phone_number")
    private String phoneNumber;
}