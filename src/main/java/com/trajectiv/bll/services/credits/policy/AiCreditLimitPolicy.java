package com.trajectiv.bll.services.credits.policy;

import com.trajectiv.bll.dto.billing.EffectiveEntitlementBllDto;
import com.trajectiv.bll.exceptions.EntitlementConfigurationException;
import org.springframework.stereotype.Component;

@Component
public class AiCreditLimitPolicy {

    public int resolveMonthlyLimit(
            EffectiveEntitlementBllDto entitlement
    ) {
        if (!entitlement.allowed()) {
            return 0;
        }

        Integer monthlyQuota =
                entitlement.quotaMonthly();

        if (monthlyQuota == null) {
            throw new EntitlementConfigurationException(
                    "AI credit entitlement requires a finite monthly quota."
            );
        }

        return monthlyQuota;
    }
}