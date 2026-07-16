package com.trajectiv.dl.entities.organization;

import com.trajectiv.dl.entities.User;
import com.trajectiv.dl.enums.organization.OrganizationInvitationStatus;
import com.trajectiv.dl.enums.organization.OrganizationRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Getter
@Entity
@Table(
        name = "organization_invitations",
        indexes = {
                @Index(
                        name = "idx_org_invitations_organization_id",
                        columnList = "organization_id"
                ),
                @Index(
                        name = "idx_org_invitations_email",
                        columnList = "email"
                ),
                @Index(
                        name = "idx_org_invitations_status",
                        columnList = "status"
                ),
                @Index(
                        name = "idx_org_invitations_expires_at",
                        columnList = "expires_at"
                )
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_org_invitation_token_hash",
                        columnNames = "token_hash"
                )
        }
)
@ToString(
        exclude = {
                "organization",
                "invitedByUser",
                "acceptedByUser"
        }
)
public class OrganizationInvitation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "organization_id",
            nullable = false,
            updatable = false
    )
    private Organization organization;

    @Column(
            name = "email",
            nullable = false,
            length = 320,
            updatable = false
    )
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "role",
            nullable = false,
            length = 60
    )
    private OrganizationRole role;

    @Column(
            name = "token_hash",
            nullable = false,
            unique = true,
            length = 128,
            updatable = false
    )
    private String tokenHash;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 40
    )
    private OrganizationInvitationStatus status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "invited_by_user_id",
            nullable = false,
            updatable = false
    )
    private User invitedByUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "accepted_by_user_id"
    )
    private User acceptedByUser;

    @Column(
            name = "expires_at",
            nullable = false
    )
    private Instant expiresAt;

    @Column(name = "accepted_at")
    private Instant acceptedAt;

    @Column(name = "revoked_at")
    private Instant revokedAt;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private Instant createdAt;

    @Column(
            name = "updated_at",
            nullable = false
    )
    private Instant updatedAt;

    protected OrganizationInvitation() {
    }

    private OrganizationInvitation(
            Organization organization,
            String email,
            OrganizationRole role,
            String tokenHash,
            User invitedByUser,
            Instant expiresAt
    ) {
        this.organization = Objects.requireNonNull(
                organization,
                "organization cannot be null."
        );

        this.email = normalizeEmail(email);

        this.role = Objects.requireNonNull(
                role,
                "role cannot be null."
        );

        this.tokenHash = requireTokenHash(tokenHash);

        this.invitedByUser = Objects.requireNonNull(
                invitedByUser,
                "invitedByUser cannot be null."
        );

        this.expiresAt = Objects.requireNonNull(
                expiresAt,
                "expiresAt cannot be null."
        );

        this.status =
                OrganizationInvitationStatus.PENDING;
    }

    public static OrganizationInvitation create(
            Organization organization,
            String email,
            OrganizationRole role,
            String tokenHash,
            User invitedByUser,
            Instant expiresAt
    ) {
        if (
                role ==
                        OrganizationRole.ORGANIZATION_OWNER
        ) {
            throw new IllegalArgumentException(
                    "Organization ownership cannot be granted through an invitation."
            );
        }

        return new OrganizationInvitation(
                organization,
                email,
                role,
                tokenHash,
                invitedByUser,
                expiresAt
        );
    }

    public void accept(
            User user,
            Instant acceptedAt
    ) {
        if (
                status !=
                        OrganizationInvitationStatus.PENDING
        ) {
            throw new IllegalStateException(
                    "Only a pending invitation can be accepted."
            );
        }

        if (acceptedAt.isAfter(expiresAt)) {
            this.status =
                    OrganizationInvitationStatus.EXPIRED;

            throw new IllegalStateException(
                    "Invitation has expired."
            );
        }

        if (
                !email.equalsIgnoreCase(
                        user.getEmail()
                )
        ) {
            throw new IllegalArgumentException(
                    "Authenticated user email does not match invitation email."
            );
        }

        this.status =
                OrganizationInvitationStatus.ACCEPTED;

        this.acceptedByUser = user;
        this.acceptedAt = acceptedAt;
    }

    public void revoke(
            Instant revokedAt
    ) {
        if (
                status !=
                        OrganizationInvitationStatus.PENDING
        ) {
            throw new IllegalStateException(
                    "Only a pending invitation can be revoked."
            );
        }

        this.status =
                OrganizationInvitationStatus.REVOKED;

        this.revokedAt = Objects.requireNonNull(
                revokedAt
        );
    }

    public void markExpired() {
        if (
                status ==
                        OrganizationInvitationStatus.PENDING
        ) {
            status =
                    OrganizationInvitationStatus.EXPIRED;
        }
    }

    public boolean isPendingAt(
            Instant instant
    ) {
        return status ==
                OrganizationInvitationStatus.PENDING
                && instant.isBefore(expiresAt);
    }

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();

        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    private static String normalizeEmail(
            String value
    ) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "Invitation email cannot be blank."
            );
        }

        return value.trim().toLowerCase();
    }

    private static String requireTokenHash(
            String value
    ) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "Invitation token hash cannot be blank."
            );
        }

        return value.trim();
    }
}