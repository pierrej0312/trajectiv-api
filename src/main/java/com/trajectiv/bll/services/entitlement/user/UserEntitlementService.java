package com.trajectiv.bll.services.entitlement.user;

import com.trajectiv.bll.dto.billing.EffectiveEntitlementBllDto;
import com.trajectiv.dl.enums.FeatureKey;

import java.util.Set;
import java.util.UUID;

public interface UserEntitlementService {

    Set<EffectiveEntitlementBllDto> resolveForUser(
            UUID userId
    );

    EffectiveEntitlementBllDto resolveForUser(
            UUID userId,
            FeatureKey featureKey
    );
}