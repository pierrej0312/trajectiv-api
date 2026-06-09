package com.trajectiv.api.dto.me;

public record UpdatedMeProfileResponseApiDto(
        MeProfileApiDto profile,
        MeOnboardingApiDto onboarding
) {
}
