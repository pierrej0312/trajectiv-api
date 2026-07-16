package com.trajectiv.bll.services.entitlement.user;

import com.trajectiv.bll.dto.billing.EffectiveEntitlementBllDto;
import com.trajectiv.bll.mappers.billing.EntitlementBllMapper;
import com.trajectiv.bll.models.entitlement.EntitlementDefinition;
import com.trajectiv.bll.models.entitlement.EntitlementOverride;
import com.trajectiv.bll.services.entitlement.policy.EntitlementMergePolicy;
import com.trajectiv.bll.services.subscription.user.UserSubscriptionService;
import com.trajectiv.dl.entities.billing.UserSubscription;
import com.trajectiv.dl.enums.FeatureKey;
import com.trajectiv.dl.repositories.billing.PlanEntitlementRepository;
import com.trajectiv.dl.repositories.billing.UserEntitlementGrantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserEntitlementServiceImpl
        implements UserEntitlementService {

    private final UserSubscriptionService subscriptionService;

    private final PlanEntitlementRepository
            planEntitlementRepository;

    private final UserEntitlementGrantRepository
            grantRepository;

    private final EntitlementBllMapper mapper;
    private final EntitlementMergePolicy mergePolicy;
    private final Clock clock;

    @Override
    public Set<EffectiveEntitlementBllDto> resolveForUser(
            UUID userId
    ) {
        Objects.requireNonNull(
                userId,
                "userId cannot be null."
        );

        UserSubscription subscription =
                subscriptionService.getCurrent(userId);

        Map<FeatureKey, EntitlementDefinition>
                planDefinitions =
                planEntitlementRepository
                        .findAllByPlanCode(
                                subscription.getPlanCode()
                        )
                        .stream()
                        .map(mapper::toDefinition)
                        .collect(
                                Collectors.toMap(
                                        EntitlementDefinition
                                                ::featureKey,
                                        Function.identity()
                                )
                        );

        Instant now = Instant.now(clock);

        List<EntitlementOverride> overrides =
                grantRepository
                        .findAllActiveByUserId(
                                userId,
                                now
                        )
                        .stream()
                        .map(mapper::toOverride)
                        .toList();

        EnumSet<FeatureKey> featureKeys =
                EnumSet.noneOf(
                        FeatureKey.class
                );

        featureKeys.addAll(planDefinitions.keySet());

        overrides.stream()
                .map(
                        EntitlementOverride::featureKey
                )
                .forEach(featureKeys::add);

        return featureKeys.stream()
                .map(
                        featureKey ->
                                mergePolicy.resolve(
                                        featureKey,
                                        planDefinitions.get(
                                                featureKey
                                        ),
                                        overrides,
                                        now
                                )
                )
                .collect(
                        Collectors.toUnmodifiableSet()
                );
    }

    @Override
    public EffectiveEntitlementBllDto resolveForUser(
            UUID userId,
            FeatureKey featureKey
    ) {
        Objects.requireNonNull(
                featureKey,
                "featureKey cannot be null."
        );

        UserSubscription subscription =
                subscriptionService.getCurrent(userId);

        EntitlementDefinition definition =
                planEntitlementRepository
                        .findByPlanCodeAndFeatureKey(
                                subscription.getPlanCode(),
                                featureKey
                        )
                        .map(mapper::toDefinition)
                        .orElse(null);

        Instant now = Instant.now(clock);

        List<EntitlementOverride> overrides =
                grantRepository
                        .findAllActiveByUserIdAndFeatureKey(
                                userId,
                                featureKey,
                                now
                        )
                        .stream()
                        .map(mapper::toOverride)
                        .toList();

        return mergePolicy.resolve(
                featureKey,
                definition,
                overrides,
                now
        );
    }
}