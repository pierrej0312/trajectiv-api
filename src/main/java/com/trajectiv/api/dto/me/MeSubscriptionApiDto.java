package com.trajectiv.api.dto.me;

import com.trajectiv.dl.enums.SubscriptionPlan;
import com.trajectiv.dl.enums.SubscriptionStatus;

public record MeSubscriptionApiDto(
        SubscriptionPlan plan,
        SubscriptionStatus status
) {
}
