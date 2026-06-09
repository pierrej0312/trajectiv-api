package com.trajectiv.api.dto.me;

import com.trajectiv.bll.dto.me.OnboardingMissingField;
import com.trajectiv.dl.enums.OnboardingStatus;

import java.time.Instant;
import java.util.List;

public record MeOnboardingApiDto(
        OnboardingStatus status,
        Instant completedAt,
        List<OnboardingMissingField> missingFields
) {
}
