package com.trajectiv.bll.services.entitlement.policy;

import com.trajectiv.bll.dto.billing.EffectiveEntitlementBllDto;
import com.trajectiv.bll.models.entitlement.EffectiveEntitlementSource;
import com.trajectiv.bll.models.entitlement.EntitlementDefinition;
import com.trajectiv.bll.models.entitlement.EntitlementOverride;
import com.trajectiv.dl.enums.entitlement.EntitlementSource;
import com.trajectiv.dl.enums.FeatureKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collection;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class EntitlementMergePolicy {

    private final EntitlementPriorityPolicy priorityPolicy;

    public EffectiveEntitlementBllDto resolve(
            FeatureKey featureKey,
            EntitlementDefinition planDefinition,
            Collection<EntitlementOverride> overrides,
            Instant at
    ) {
        Objects.requireNonNull(
                featureKey,
                "featureKey cannot be null."
        );

        Objects.requireNonNull(
                overrides,
                "overrides cannot be null."
        );

        Objects.requireNonNull(
                at,
                "at cannot be null."
        );

        EffectiveEntitlementBllDto base =
                planDefinition != null
                        ? fromPlan(planDefinition)
                        : EffectiveEntitlementBllDto.denied(
                        featureKey
                );

        EntitlementOverride winningOverride =
                overrides.stream()
                        .filter(
                                override ->
                                        override.featureKey()
                                                == featureKey
                        )
                        .filter(
                                override ->
                                        override.isActiveAt(at)
                        )
                        .sorted(
                                priorityPolicy
                                        .highestPriorityFirst()
                        )
                        .findFirst()
                        .orElse(null);

        if (winningOverride == null) {
            return base;
        }

        return applyOverride(
                base,
                winningOverride
        );
    }

    private EffectiveEntitlementBllDto fromPlan(
            EntitlementDefinition definition
    ) {
        return new EffectiveEntitlementBllDto(
                definition.featureKey(),
                definition.allowed(),
                definition.quotaMonthly(),
                definition.quotaTotal(),
                definition.maxItems(),
                definition.resetPeriod(),
                EffectiveEntitlementSource.PLAN
        );
    }

    private EffectiveEntitlementBllDto applyOverride(
            EffectiveEntitlementBllDto base,
            EntitlementOverride override
    ) {
        return new EffectiveEntitlementBllDto(
                base.featureKey(),

                override.allowed() != null
                        ? override.allowed()
                        : base.allowed(),

                override.quotaMonthly() != null
                        ? override.quotaMonthly()
                        : base.quotaMonthly(),

                override.quotaTotal() != null
                        ? override.quotaTotal()
                        : base.quotaTotal(),

                override.maxItems() != null
                        ? override.maxItems()
                        : base.maxItems(),

                base.resetPeriod(),

                mapSource(override.source())
        );
    }

    private EffectiveEntitlementSource mapSource(
            EntitlementSource source
    ) {
        return switch (source) {
            case MIGRATION ->
                    EffectiveEntitlementSource.MIGRATION;

            case BETA_PROGRAM ->
                    EffectiveEntitlementSource.BETA_PROGRAM;

            case PROMOTION ->
                    EffectiveEntitlementSource.PROMOTION;

            case PARTNER_GRANT ->
                    EffectiveEntitlementSource.PARTNER_GRANT;

            case COMPENSATION ->
                    EffectiveEntitlementSource.COMPENSATION;

            case PURCHASED_PACK ->
                    EffectiveEntitlementSource.PURCHASED_PACK;

            case ADMIN_GRANT ->
                    EffectiveEntitlementSource.ADMIN_GRANT;
        };
    }
}