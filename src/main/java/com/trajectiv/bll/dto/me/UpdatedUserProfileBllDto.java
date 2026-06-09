package com.trajectiv.bll.dto.me;

public record UpdatedUserProfileBllDto(
        UserProfileBllDto profile,
        MeOnboardingBllDto onboarding
) {
}
