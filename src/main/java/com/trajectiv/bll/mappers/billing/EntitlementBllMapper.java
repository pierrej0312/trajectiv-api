package com.trajectiv.bll.mappers.billing;

import com.trajectiv.bll.models.entitlement.EntitlementDefinition;
import com.trajectiv.bll.models.entitlement.EntitlementOverride;
import com.trajectiv.dl.entities.billing.OrganizationEntitlementGrant;
import com.trajectiv.dl.entities.billing.PlanEntitlement;
import com.trajectiv.dl.entities.billing.UserEntitlementGrant;
import org.springframework.stereotype.Component;

@Component
public class EntitlementBllMapper {

    public EntitlementDefinition toDefinition(
            PlanEntitlement entity
    ) {
        return new EntitlementDefinition(
                entity.getFeatureKey(),
                entity.isAllowed(),
                entity.getQuotaMonthly(),
                entity.getQuotaTotal(),
                entity.getMaxItems(),
                entity.getResetPeriod()
        );
    }

    public EntitlementOverride toOverride(
            UserEntitlementGrant entity
    ) {
        return new EntitlementOverride(
                entity.getId(),
                entity.getFeatureKey(),
                entity.getSource(),
                entity.getAllowed(),
                entity.getQuotaMonthly(),
                entity.getQuotaTotal(),
                entity.getMaxItems(),
                entity.getValidFrom(),
                entity.getValidUntil()
        );
    }

    public EntitlementOverride toOverride(
            OrganizationEntitlementGrant entity
    ) {
        return new EntitlementOverride(
                entity.getId(),
                entity.getFeatureKey(),
                entity.getSource(),
                entity.getAllowed(),
                entity.getQuotaMonthly(),
                entity.getQuotaTotal(),
                entity.getMaxItems(),
                entity.getValidFrom(),
                entity.getValidUntil()
        );
    }
}