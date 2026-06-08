package com.trajectiv.bll.dto.me;

import com.trajectiv.dl.enums.SubscriptionPlan;
import com.trajectiv.dl.enums.SubscriptionStatus;

public record SubscriptionBllDto(
        SubscriptionPlan plan,
        SubscriptionStatus status
) {
}
