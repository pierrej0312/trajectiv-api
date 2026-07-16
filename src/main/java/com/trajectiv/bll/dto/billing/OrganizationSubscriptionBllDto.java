package com.trajectiv.bll.dto.billing;

import com.trajectiv.dl.enums.billing.SubscriptionStatus;

import java.time.Instant;
import java.util.UUID;

public record OrganizationSubscriptionBllDto(
        UUID id,
        UUID organizationId,
        String planCode,
        SubscriptionStatus status,
        Instant currentPeriodStart,
        Instant currentPeriodEnd,
        boolean cancelAtPeriodEnd,
        Integer seatLimit
) {
}