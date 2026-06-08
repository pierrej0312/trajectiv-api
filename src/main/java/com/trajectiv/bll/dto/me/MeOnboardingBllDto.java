package com.trajectiv.bll.dto.me;

import com.trajectiv.dl.enums.OnboardingStatus;

import java.time.Instant;
import java.util.List;

public record MeOnboardingBllDto(
        OnboardingStatus status,
        Instant completedAt,
        List<OnboardingMissingField> missingFields
) {
}
