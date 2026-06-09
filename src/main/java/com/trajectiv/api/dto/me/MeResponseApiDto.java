package com.trajectiv.api.dto.me;

import com.trajectiv.dl.enums.UserStatus;

import java.util.UUID;

public record MeResponseApiDto(
        UUID id,
        String keycloakSubject,
        String email,
        boolean emailVerified,
        String firstName,
        String lastName,
        String displayName,
        String avatarUrl,
        UserStatus status,
        MeOnboardingApiDto onboarding,
        MeProfileApiDto profile,
        MeSubscriptionApiDto subscription,
        MeCreditsApiDto credits
) {
}
