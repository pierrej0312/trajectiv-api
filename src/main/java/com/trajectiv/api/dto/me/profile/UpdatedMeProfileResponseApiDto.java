package com.trajectiv.api.dto.me.profile;

import com.trajectiv.api.dto.me.onboarding.MeOnboardingApiDto;

public record UpdatedMeProfileResponseApiDto(
        MeProfileApiDto profile,
        MeOnboardingApiDto onboarding
) {
}
