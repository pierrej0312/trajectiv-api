package com.trajectiv.api.mappers;

import com.trajectiv.api.dto.me.EffectiveEntitlementApiDto;
import com.trajectiv.bll.dto.billing.EffectiveEntitlementBllDto;
import org.springframework.stereotype.Component;

@Component
public class EffectiveEntitlementApiMapper {

    public EffectiveEntitlementApiDto toApiDto(
            EffectiveEntitlementBllDto entitlement
    ) {
        return new EffectiveEntitlementApiDto(
                entitlement.featureKey().name(),
                entitlement.allowed(),
                entitlement.quotaMonthly(),
                entitlement.quotaTotal(),
                entitlement.maxItems(),
                entitlement.resetPeriod() == null
                        ? null
                        : entitlement.resetPeriod().name(),
                entitlement.source().name()
        );
    }
}