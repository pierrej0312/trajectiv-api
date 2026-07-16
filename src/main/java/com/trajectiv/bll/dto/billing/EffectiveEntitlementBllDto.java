package com.trajectiv.bll.dto.billing;

import com.trajectiv.bll.models.entitlement.EffectiveEntitlementSource;
import com.trajectiv.dl.enums.entitlement.EntitlementResetPeriod;
import com.trajectiv.dl.enums.FeatureKey;

import java.util.Objects;

public record EffectiveEntitlementBllDto(
        FeatureKey featureKey,
        boolean allowed,
        Integer quotaMonthly,
        Integer quotaTotal,
        Integer maxItems,
        EntitlementResetPeriod resetPeriod,
        EffectiveEntitlementSource source
) {

    public EffectiveEntitlementBllDto {
        Objects.requireNonNull(
                featureKey,
                "featureKey cannot be null."
        );

        Objects.requireNonNull(
                source,
                "source cannot be null."
        );

        requireNonNegative(quotaMonthly, "quotaMonthly");
        requireNonNegative(quotaTotal, "quotaTotal");
        requireNonNegative(maxItems, "maxItems");
    }

    public static EffectiveEntitlementBllDto denied(
            FeatureKey featureKey
    ) {
        return new EffectiveEntitlementBllDto(
                featureKey,
                false,
                null,
                null,
                null,
                null,
                EffectiveEntitlementSource.DEFAULT_DENY
        );
    }

    public boolean hasUnlimitedMonthlyQuota() {
        return allowed && quotaMonthly == null;
    }

    public boolean hasUnlimitedTotalQuota() {
        return allowed && quotaTotal == null;
    }

    public boolean hasUnlimitedItems() {
        return allowed && maxItems == null;
    }

    private static void requireNonNegative(
            Integer value,
            String fieldName
    ) {
        if (value != null && value < 0) {
            throw new IllegalArgumentException(
                    fieldName + " cannot be negative."
            );
        }
    }
}