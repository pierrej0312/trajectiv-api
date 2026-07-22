package com.trajectiv.dl.entities.organization;

import com.trajectiv.dl.entities.User;
import com.trajectiv.dl.enums.organization.OrganizationMemberStatus;
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
        name = "organization_members",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_organization_members_user_org",
                        columnNames = {
                                "user_id",
                                "organization_id"
                        }
                )
        },
        indexes = {
                @Index(
                        name = "idx_organization_members_user_id",
                        columnList = "user_id"
                ),
                @Index(
                        name = "idx_organization_members_organization_id",
                        columnList = "organization_id"
                ),
                @Index(
                        name = "idx_organization_members_status",
                        columnList = "status"
                ),
                @Index(
                        name = "idx_organization_members_role",
                        columnList = "role"
                )
        }
)
@ToString(exclude = {
        "user",
        "organization"
})
public class OrganizationMember {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "user_id",
            nullable = false,
            updatable = false
    )
    private User user;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "organization_id",
            nullable = false,
            updatable = false
    )
    private Organization organization;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "role",
            nullable = false,
            length = 60
    )
    private OrganizationRole role;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 40
    )
    private OrganizationMemberStatus status;

    @Column(
            name = "joined_at"
    )
    private Instant joinedAt;

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

    protected OrganizationMember() {
    }

    private OrganizationMember(
            User user,
            Organization organization,
            OrganizationRole role,
            OrganizationMemberStatus status
    ) {
        this.user = requireUser(user);
        this.organization =
                requireOrganization(organization);
        this.role = requireRole(role);
        this.status = status;
        this.joinedAt =
                status == OrganizationMemberStatus.ACTIVE
                        ? Instant.now()
                        : null;
    }

    public static OrganizationMember createActive(
            User user,
            Organization organization,
            OrganizationRole role
    ) {
        return new OrganizationMember(
                user,
                organization,
                role,
                OrganizationMemberStatus.ACTIVE
        );
    }

    public void changeRole(
            OrganizationRole role
    ) {
        if (!isActive()) {
            throw new IllegalStateException(
                    "Cannot change the role of an inactive member."
            );
        }

        this.role = requireRole(role);
        this.updatedAt = Instant.now();
    }

    public void suspend() {
        if (
                this.status !=
                        OrganizationMemberStatus.ACTIVE
        ) {
            throw new IllegalStateException(
                    "Only an active organization member can be suspended."
            );
        }

        this.status =
                OrganizationMemberStatus.SUSPENDED;
    }

    public void reactivate() {
        if (
                this.status !=
                        OrganizationMemberStatus.SUSPENDED
        ) {
            throw new IllegalStateException(
                    "Only a suspended organization member can be reactivated."
            );
        }

        this.status =
                OrganizationMemberStatus.ACTIVE;
    }

    public void remove() {
        if (
                this.status ==
                        OrganizationMemberStatus.REMOVED
        ) {
            throw new IllegalStateException(
                    "Organization member is already removed."
            );
        }

        this.status =
                OrganizationMemberStatus.REMOVED;
    }

    public boolean isActive() {
        return this.status ==
                OrganizationMemberStatus.ACTIVE;
    }

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();

        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    private static User requireUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException(
                    "User cannot be null."
            );
        }

        return user;
    }

    private static Organization requireOrganization(
            Organization organization
    ) {
        if (organization == null) {
            throw new IllegalArgumentException(
                    "Organization cannot be null."
            );
        }

        return organization;
    }

    private static OrganizationRole requireRole(
            OrganizationRole role
    ) {
        if (role == null) {
            throw new IllegalArgumentException(
                    "Organization role cannot be null."
            );
        }

        return role;
    }

    public void restore(
            OrganizationRole role,
            Instant joinedAt
    ) {
        if (status != OrganizationMemberStatus.REMOVED) {
            throw new IllegalStateException(
                    "Only a removed membership can be restored."
            );
        }

        this.role = Objects.requireNonNull(
                role,
                "role cannot be null."
        );

        this.joinedAt = Objects.requireNonNull(
                joinedAt,
                "joinedAt cannot be null."
        );

        this.status = OrganizationMemberStatus.ACTIVE;
    }
}