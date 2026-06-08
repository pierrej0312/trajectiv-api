package com.trajectiv.repositories;

import com.trajectiv.dl.entities.UserProfile;
import com.trajectiv.dl.enums.OnboardingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {

    Optional<UserProfile> findByUserId(UUID userId);

    boolean existsByUserId(UUID userId);

    long countByOnboardingStatus(OnboardingStatus onboardingStatus);
}
