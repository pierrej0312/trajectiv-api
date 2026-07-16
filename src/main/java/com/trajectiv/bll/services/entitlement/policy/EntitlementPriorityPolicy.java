package com.trajectiv.bll.services.entitlement.policy;

import com.trajectiv.bll.models.entitlement.EntitlementOverride;
import com.trajectiv.dl.enums.entitlement.EntitlementSource;
import org.springframework.stereotype.Component;

import java.util.Comparator;

@Component
public class EntitlementPriorityPolicy {

    public int priorityOf(
            EntitlementSource source
    ) {
        return switch (source) {
            case MIGRATION -> 10;
            case BETA_PROGRAM -> 20;
            case PROMOTION -> 30;
            case PARTNER_GRANT -> 40;
            case COMPENSATION -> 50;
            case PURCHASED_PACK -> 60;
            case ADMIN_GRANT -> 100;
        };
    }

    public Comparator<EntitlementOverride>
    highestPriorityFirst() {
        return Comparator
                .comparingInt(
                        (
                                EntitlementOverride override
                        ) -> priorityOf(
                                override.source()
                        )
                )
                .reversed()
                .thenComparing(
                        EntitlementOverride::validFrom,
                        Comparator.nullsLast(
                                Comparator.reverseOrder()
                        )
                )
                .thenComparing(
                        EntitlementOverride::id
                );
    }
}