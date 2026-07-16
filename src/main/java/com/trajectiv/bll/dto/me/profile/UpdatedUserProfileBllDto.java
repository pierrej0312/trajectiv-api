package com.trajectiv.bll.dto.me.profile;

import com.trajectiv.bll.dto.me.onboarding.MeOnboardingBllDto;

public record UpdatedUserProfileBllDto(
        UserProfileBllDto profile,
        MeOnboardingBllDto onboarding
) {
}
