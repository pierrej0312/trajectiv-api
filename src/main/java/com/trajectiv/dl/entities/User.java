package com.trajectiv.dl.entities;

import com.trajectiv.dl.enums.UserStatus;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Entity
@Table(
        name = "users",
        indexes = {
                @Index(name = "idx_users_keycloak_subject", columnList = "keycloak_subject"),
                @Index(name = "idx_users_email", columnList = "email"),
                @Index(name = "idx_users_status", columnList = "status"),
                @Index(name = "idx_users_deleted_at", columnList = "deleted_at")
        }
)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "keycloak_subject", nullable = false, unique = true, length = 255)
    private String keycloakSubject;

    @Column(name = "email", nullable = false, unique = true, length = 320)
    private String email;

    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified = false;

    @Column(name = "first_name", length = 120)
    private String firstName;

    @Column(name = "last_name", length = 120)
    private String lastName;

    @Column(name = "display_name", length = 180)
    private String displayName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 40)
    private UserStatus status = UserStatus.ACTIVE;

    @Setter
    @Column(name = "last_login_at")
    private Instant lastLoginAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    private User(
            String keycloakSubject,
            String email,
            boolean emailVerified,
            String firstName,
            String lastName,
            String displayName
    ) {
        this.keycloakSubject = keycloakSubject;
        this.email = email;
        this.emailVerified = emailVerified;
        this.firstName = firstName;
        this.lastName = lastName;
        this.displayName = displayName;
        this.status = UserStatus.ACTIVE;
        this.lastLoginAt = Instant.now();
    }

    public User() {
    }

    public static User createFromKeycloak(
            String keycloakSubject,
            String email,
            boolean emailVerified,
            String firstName,
            String lastName,
            String displayName
    ) {
        return new User(
                keycloakSubject,
                email,
                emailVerified,
                firstName,
                lastName,
                displayName
        );
    }

    public void updateFromKeycloak(
            String email,
            boolean emailVerified,
            String firstName,
            String lastName,
            String displayName
    ) {
        this.email = email;
        this.emailVerified = emailVerified;
        this.firstName = firstName;
        this.lastName = lastName;
        this.displayName = displayName;
        this.lastLoginAt = Instant.now();
    }

    public void disable() {
        this.status = UserStatus.DISABLED;
    }

    public void activate() {
        this.status = UserStatus.ACTIVE;
    }

    public void softDelete() {
        this.status = UserStatus.DELETED;
        this.deletedAt = Instant.now();
    }

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;

        if (this.status == null) {
            this.status = UserStatus.ACTIVE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }
}