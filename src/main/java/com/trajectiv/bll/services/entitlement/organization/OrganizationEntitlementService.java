package com.trajectiv.bll.services.entitlement.organization;

import com.trajectiv.bll.dto.billing.EffectiveEntitlementBllDto;
import com.trajectiv.dl.enums.FeatureKey;

import java.util.Set;
import java.util.UUID;

public interface OrganizationEntitlementService {

    Set<EffectiveEntitlementBllDto>
    resolveForOrganization(
            UUID organizationId
    );

    EffectiveEntitlementBllDto
    resolveForOrganization(
            UUID organizationId,
            FeatureKey featureKey
    );
}