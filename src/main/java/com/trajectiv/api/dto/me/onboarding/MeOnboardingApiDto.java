package com.trajectiv.api.dto.me.onboarding;

import com.trajectiv.bll.dto.me.onboarding.OnboardingMissingField;

import java.time.Instant;
import java.util.List;

public record MeOnboardingApiDto(
        OnboardingStatusApiDto status,
        Instant completedAt,
        List<OnboardingMissingField> missingFields
) {

    public MeOnboardingApiDto {
        missingFields = missingFields == null
                ? List.of()
                : List.copyOf(missingFields);
    }
}