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
@EqualsAndHashCode
@ToString(exclude = {"profile", "subscription", "aiCreditWallet", "files"})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Setter
    @Column(name = "keycloak_subject", nullable = false, unique = true, length = 255)
    private String keycloakSubject;

    @Setter
    @Column(name = "email", nullable = false, unique = true, length = 320)
    private String email;

    @Setter
    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified = false;

    @Setter
    @Column(name = "first_name", length = 120)
    private String firstName;

    @Setter
    @Column(name = "last_name", length = 120)
    private String lastName;

    @Setter
    @Column(name = "display_name", length = 180)
    private String displayName;

    @Setter
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

    @Setter
    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Setter
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private UserProfile profile;

    @Setter
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Subscription subscription;

    @Setter
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private AiCreditWallet aiCreditWallet;

    @Setter
    @OneToMany(mappedBy = "ownerUser", fetch = FetchType.LAZY)
    private List<UserFile> files = new ArrayList<>();

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