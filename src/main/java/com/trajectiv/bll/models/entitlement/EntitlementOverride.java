package com.trajectiv.bll.models.entitlement;

import com.trajectiv.dl.enums.entitlement.EntitlementSource;
import com.trajectiv.dl.enums.FeatureKey;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record EntitlementOverride(
        UUID id,
        FeatureKey featureKey,
        EntitlementSource source,
        Boolean allowed,
        Integer quotaMonthly,
        Integer quotaTotal,
        Integer maxItems,
        Instant validFrom,
        Instant validUntil
) {

    public EntitlementOverride {
        Objects.requireNonNull(id, "id cannot be null.");
        Objects.requireNonNull(
                featureKey,
                "featureKey cannot be null."
        );
        Objects.requireNonNull(
                source,
                "source cannot be null."
        );

        if (
                validFrom != null &&
                        validUntil != null &&
                        validUntil.isBefore(validFrom)
        ) {
            throw new IllegalArgumentException(
                    "validUntil cannot precede validFrom."
            );
        }
    }

    public boolean isActiveAt(Instant instant) {
        Objects.requireNonNull(
                instant,
                "instant cannot be null."
        );

        return (
                validFrom == null ||
                        !instant.isBefore(validFrom)
        ) && (
                validUntil == null ||
                        instant.isBefore(validUntil)
        );
    }
}