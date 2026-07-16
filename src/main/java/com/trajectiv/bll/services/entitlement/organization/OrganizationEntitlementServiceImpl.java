package com.trajectiv.bll.services.entitlement.organization;

import com.trajectiv.bll.dto.billing.EffectiveEntitlementBllDto;
import com.trajectiv.bll.mappers.billing.EntitlementBllMapper;
import com.trajectiv.bll.models.entitlement.EntitlementDefinition;
import com.trajectiv.bll.models.entitlement.EntitlementOverride;
import com.trajectiv.bll.services.entitlement.policy.EntitlementMergePolicy;
import com.trajectiv.bll.services.subscription.organization.OrganizationSubscriptionService;
import com.trajectiv.dl.entities.billing.OrganizationSubscription;
import com.trajectiv.dl.enums.FeatureKey;
import com.trajectiv.dl.repositories.billing.OrganizationEntitlementGrantRepository;
import com.trajectiv.dl.repositories.billing.PlanEntitlementRepository;
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
public class OrganizationEntitlementServiceImpl
        implements OrganizationEntitlementService {

    private final OrganizationSubscriptionService
            subscriptionService;

    private final PlanEntitlementRepository
            planEntitlementRepository;

    private final OrganizationEntitlementGrantRepository
            grantRepository;

    private final EntitlementBllMapper mapper;
    private final EntitlementMergePolicy mergePolicy;
    private final Clock clock;

    @Override
    public Set<EffectiveEntitlementBllDto>
    resolveForOrganization(
            UUID organizationId
    ) {
        OrganizationSubscription subscription =
                subscriptionService.getCurrent(
                        organizationId
                );

        Map<FeatureKey, EntitlementDefinition>
                definitions =
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
                        .findAllActiveByOrganizationId(
                                organizationId,
                                now
                        )
                        .stream()
                        .map(mapper::toOverride)
                        .toList();

        EnumSet<FeatureKey> featureKeys =
                EnumSet.noneOf(
                        FeatureKey.class
                );

        featureKeys.addAll(definitions.keySet());

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
                                        definitions.get(
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
    public EffectiveEntitlementBllDto
    resolveForOrganization(
            UUID organizationId,
            FeatureKey featureKey
    ) {
        OrganizationSubscription subscription =
                subscriptionService.getCurrent(
                        organizationId
                );

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
                        .findAllActiveByOrganizationIdAndFeatureKey(
                                organizationId,
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