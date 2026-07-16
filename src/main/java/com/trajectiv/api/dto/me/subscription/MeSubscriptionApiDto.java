package com.trajectiv.api.dto.me.subscription;

import java.time.Instant;

public record MeSubscriptionApiDto(
        String planCode,
        SubscriptionStatusApiDto status,
        Instant currentPeriodEnd,
        boolean cancelAtPeriodEnd,
        Instant trialEnd
) {
}