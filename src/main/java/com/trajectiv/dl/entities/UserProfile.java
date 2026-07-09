package com.trajectiv.dl.entities;

import com.trajectiv.dl.enums.CareerGoal;
import com.trajectiv.dl.enums.ExperienceLevel;
import com.trajectiv.dl.enums.OnboardingStatus;
import com.trajectiv.dl.enums.TargetRoleSource;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@Table(
        name = "user_profiles",
        indexes = {
                @Index(name = "idx_user_profiles_user_id", columnList = "user_id"),
                @Index(name = "idx_user_profiles_avatar_file_id", columnList = "avatar_file_id"),
                @Index(name = "idx_user_profiles_onboarding_status", columnList = "onboarding_status"),
                @Index(name = "idx_user_profiles_career_goal", columnList = "career_goal"),
                @Index(name = "idx_user_profiles_target_role_id", columnList = "target_role_id"),
                @Index(name = "idx_user_profiles_target_role_source", columnList = "target_role_source"),
                @Index(name = "idx_user_profiles_target_role_label", columnList = "target_role_label")
        }
)
@ToString(exclude = {"user", "avatarFile", "targetRole"})
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true, updatable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "avatar_file_id")
    private UserFile avatarFile;

    @Column(name = "avatar_url", length = 1000)
    private String avatarUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "career_goal", length = 60)
    private CareerGoal careerGoal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_role_id")
    private JobRole targetRole;

    @Column(name = "target_role_label", length = 180)
    private String targetRoleLabel;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_role_source", nullable = false, length = 40)
    private TargetRoleSource targetRoleSource = TargetRoleSource.CUSTOM;

    @Enumerated(EnumType.STRING)
    @Column(name = "experience_level", length = 60)
    private ExperienceLevel experienceLevel;

    @Column(name = "preferred_language", nullable = false, length = 10)
    private String preferredLanguage = "fr";

    @Enumerated(EnumType.STRING)
    @Column(name = "onboarding_status", nullable = false, length = 40)
    private OnboardingStatus onboardingStatus = OnboardingStatus.NOT_STARTED;

    @Column(name = "onboarding_completed_at")
    private Instant onboardingCompletedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected UserProfile() {
    }

    private UserProfile(User user) {
        this.user = user;
        this.preferredLanguage = "fr";
        this.onboardingStatus = OnboardingStatus.NOT_STARTED;
        this.targetRoleSource = TargetRoleSource.CUSTOM;
    }

    public static UserProfile createDefault(User user) {
        return new UserProfile(user);
    }

    public void updateProfile(
            CareerGoal careerGoal,
            JobRole targetRole,
            String targetRoleLabel,
            TargetRoleSource targetRoleSource,
            ExperienceLevel experienceLevel,
            String preferredLanguage
    ) {
        if (careerGoal != null) {
            this.careerGoal = careerGoal;
        }

        updateTargetRole(targetRole, targetRoleLabel, targetRoleSource);

        if (experienceLevel != null) {
            this.experienceLevel = experienceLevel;
        }

        if (preferredLanguage != null && !preferredLanguage.isBlank()) {
            this.preferredLanguage = preferredLanguage.trim().toLowerCase();
        }

        if (this.onboardingStatus != OnboardingStatus.COMPLETED) {
            this.onboardingStatus = OnboardingStatus.IN_PROGRESS;
        }
    }

    public void updateTargetRole(
            JobRole targetRole,
            String targetRoleLabel,
            TargetRoleSource targetRoleSource
    ) {
        if (targetRole != null) {
            this.targetRole = targetRole;
            this.targetRoleLabel = resolveCatalogRoleLabel(targetRole, targetRoleLabel);
            this.targetRoleSource = TargetRoleSource.CATALOG;
            return;
        }

        if (targetRoleLabel != null && !targetRoleLabel.isBlank()) {
            this.targetRole = null;
            this.targetRoleLabel = targetRoleLabel.trim();
            this.targetRoleSource = targetRoleSource != null
                    ? targetRoleSource
                    : TargetRoleSource.CUSTOM;
        }
    }

    public String getResolvedTargetRoleLabel() {
        if (targetRoleLabel != null && !targetRoleLabel.isBlank()) {
            return targetRoleLabel;
        }

        if (targetRole != null) {
            return targetRole.getLabel();
        }

        return null;
    }

    public void updateAvatar(UserFile avatarFile, String avatarUrl) {
        this.avatarFile = avatarFile;
        this.avatarUrl = avatarUrl;
    }

    public void removeAvatar() {
        this.avatarFile = null;
        this.avatarUrl = null;
    }

    public void completeOnboarding() {
        this.onboardingStatus = OnboardingStatus.COMPLETED;
        this.onboardingCompletedAt = Instant.now();
    }

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;

        if (this.preferredLanguage == null || this.preferredLanguage.isBlank()) {
            this.preferredLanguage = "fr";
        }

        if (this.onboardingStatus == null) {
            this.onboardingStatus = OnboardingStatus.NOT_STARTED;
        }

        if (this.targetRoleSource == null) {
            this.targetRoleSource = TargetRoleSource.CUSTOM;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();

        if (this.targetRoleSource == null) {
            this.targetRoleSource = TargetRoleSource.CUSTOM;
        }
    }

    private String resolveCatalogRoleLabel(JobRole targetRole, String targetRoleLabel) {
        return targetRole.getLabel();
    }
}