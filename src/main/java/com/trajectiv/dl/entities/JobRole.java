package com.trajectiv.dl.entities;

import com.trajectiv.dl.enums.JobRoleFamily;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@Table(
        name = "job_roles",
        indexes = {
                @Index(name = "idx_job_roles_active_sort", columnList = "active, sort_order"),
                @Index(name = "idx_job_roles_family", columnList = "family"),
                @Index(name = "idx_job_roles_label", columnList = "label")
        }
)
public class JobRole {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "slug", nullable = false, unique = true, length = 120)
    private String slug;

    @Column(name = "label", nullable = false, length = 160)
    private String label;

    @Column(name = "description", length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "family", nullable = false, length = 80)
    private JobRoleFamily family;

    @Column(name = "tags_json", nullable = false, columnDefinition = "jsonb")
    private String tagsJson = "[]";

    @Column(name = "aliases_json", nullable = false, columnDefinition = "jsonb")
    private String aliasesJson = "[]";

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected JobRole() {
    }

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();

        this.createdAt = now;
        this.updatedAt = now;

        if (this.tagsJson == null || this.tagsJson.isBlank()) {
            this.tagsJson = "[]";
        }

        if (this.aliasesJson == null || this.aliasesJson.isBlank()) {
            this.aliasesJson = "[]";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();

        if (this.tagsJson == null || this.tagsJson.isBlank()) {
            this.tagsJson = "[]";
        }

        if (this.aliasesJson == null || this.aliasesJson.isBlank()) {
            this.aliasesJson = "[]";
        }
    }
}