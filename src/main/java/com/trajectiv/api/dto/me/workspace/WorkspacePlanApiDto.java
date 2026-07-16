package com.trajectiv.api.dto.me.workspace;

import com.trajectiv.api.dto.me.subscription.SubscriptionStatusApiDto;

public record WorkspacePlanApiDto(
        String code,
        String label,
        SubscriptionStatusApiDto status
) {
}