package com.trajectiv.dl.entities;

import com.trajectiv.dl.enums.CareerGoal;
import com.trajectiv.dl.enums.ExperienceLevel;
import com.trajectiv.dl.enums.OnboardingStatus;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
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
                @Index(name = "idx_user_profiles_career_goal", columnList = "career_goal")
        }
)
@EqualsAndHashCode
@ToString(exclude = {"user", "avatarFile"})
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "avatar_file_id")
    private User avatarFile;

    @Setter
    @Column(name = "avatar_url", length = 1000)
    private String avatarUrl;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "career_goal", length = 60)
    private CareerGoal careerGoal;

    @Setter
    @Column(name = "target_role", length = 180)
    private String targetRole;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "experience_level", length = 60)
    private ExperienceLevel experienceLevel;

    @Setter
    @Column(name = "preferred_language", nullable = false, length = 10)
    private String preferredLanguage = "fr";

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "onboarding_status", nullable = false, length = 40)
    private OnboardingStatus onboardingStatus = OnboardingStatus.NOT_STARTED;

    @Setter
    @Column(name = "onboarding_completed_at")
    private Instant onboardingCompletedAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

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
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }
}