package com.trajectiv.bll.dto.billing;

import com.trajectiv.dl.enums.billing.SubscriptionStatus;

import java.time.Instant;
import java.util.UUID;

public record UserSubscriptionBllDto(
        UUID id,
        UUID userId,
        String planCode,
        SubscriptionStatus status,
        Instant currentPeriodStart,
        Instant currentPeriodEnd,
        boolean cancelAtPeriodEnd,
        Instant trialEnd
) {
}