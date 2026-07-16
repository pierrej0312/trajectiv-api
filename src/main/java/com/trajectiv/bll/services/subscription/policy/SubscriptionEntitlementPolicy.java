package com.trajectiv.bll.services.subscription.policy;

import com.trajectiv.dl.enums.billing.SubscriptionPlan;
import com.trajectiv.dl.enums.billing.SubscriptionStatus;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionEntitlementPolicy {

    private static final int FREE_AI_MONTHLY_LIMIT = 20;
    private static final int PREMIUM_AI_MONTHLY_LIMIT = 200;

    public int resolveAiMonthlyLimit(
            SubscriptionPlan plan,
            SubscriptionStatus status
    ) {
        boolean premiumEntitled =
                plan == SubscriptionPlan.PREMIUM
                        && switch (status) {
                    case ACTIVE, TRIALING -> true;
                    case PAST_DUE, CANCELED, EXPIRED -> false;
                };

        return premiumEntitled
                ? PREMIUM_AI_MONTHLY_LIMIT
                : FREE_AI_MONTHLY_LIMIT;
    }
}
