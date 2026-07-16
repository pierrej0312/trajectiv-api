package com.trajectiv.dl.entities.organization;

import com.trajectiv.dl.enums.organization.OrganizationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@Table(
        name = "organizations",
        indexes = {
                @Index(
                        name = "idx_organizations_slug",
                        columnList = "slug"
                ),
                @Index(
                        name = "idx_organizations_status",
                        columnList = "status"
                )
        }
)
@ToString
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(
            name = "slug",
            nullable = false,
            unique = true,
            length = 120,
            updatable = false
    )
    private String slug;

    @Column(
            name = "name",
            nullable = false,
            length = 180
    )
    private String name;

    @Column(
            name = "avatar_url",
            length = 1000
    )
    private String avatarUrl;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 40
    )
    private OrganizationStatus status;

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

    protected Organization() {
    }

    private Organization(
            String slug,
            String name
    ) {
        this.slug = normalizeSlug(slug);
        this.name = requireName(name);
        this.status = OrganizationStatus.ACTIVE;
    }

    public static Organization create(
            String slug,
            String name
    ) {
        return new Organization(slug, name);
    }

    public void rename(String name) {
        this.name = requireName(name);
    }

    public void updateAvatar(String avatarUrl) {
        this.avatarUrl = normalizeNullableText(avatarUrl);
    }

    public void removeAvatar() {
        this.avatarUrl = null;
    }

    public void suspend() {
        this.status = OrganizationStatus.SUSPENDED;
    }

    public void activate() {
        this.status = OrganizationStatus.ACTIVE;
    }

    public void archive() {
        this.status = OrganizationStatus.ARCHIVED;
    }

    public boolean isActive() {
        return this.status == OrganizationStatus.ACTIVE;
    }

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();

        this.createdAt = now;
        this.updatedAt = now;

        if (this.status == null) {
            this.status = OrganizationStatus.ACTIVE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    private static String requireName(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "Organization name cannot be blank."
            );
        }

        return value.trim();
    }

    private static String normalizeSlug(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "Organization slug cannot be blank."
            );
        }

        return value
                .trim()
                .toLowerCase();
    }

    private static String normalizeNullableText(
            String value
    ) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}