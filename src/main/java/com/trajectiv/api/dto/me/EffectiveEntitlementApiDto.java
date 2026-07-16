package com.trajectiv.api.dto.me;

public record EffectiveEntitlementApiDto(
        String featureKey,
        boolean allowed,
        Integer quotaMonthly,
        Integer quotaTotal,
        Integer maxItems,
        String resetPeriod,
        String source
) {
}