package com.trajectiv.bll.models.entitlement;

import com.trajectiv.dl.enums.entitlement.EntitlementResetPeriod;
import com.trajectiv.dl.enums.FeatureKey;

import java.util.Objects;

public record EntitlementDefinition(
        FeatureKey featureKey,
        boolean allowed,
        Integer quotaMonthly,
        Integer quotaTotal,
        Integer maxItems,
        EntitlementResetPeriod resetPeriod
) {

    public EntitlementDefinition {
        Objects.requireNonNull(
                featureKey,
                "featureKey cannot be null."
        );
    }
}