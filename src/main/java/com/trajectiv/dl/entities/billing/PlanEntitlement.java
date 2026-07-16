package com.trajectiv.dl.entities.billing;

import com.trajectiv.dl.enums.entitlement.EntitlementResetPeriod;
import com.trajectiv.dl.enums.FeatureKey;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.UUID;

@Getter
@Entity
@Table(
        name = "plan_entitlements",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_plan_entitlement_feature",
                        columnNames = {
                                "plan_id",
                                "feature_key"
                        }
                )
        }
)
public class PlanEntitlement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "plan_id",
            nullable = false,
            updatable = false
    )
    private Plan plan;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "feature_key",
            nullable = false,
            length = 100,
            updatable = false
    )
    private FeatureKey featureKey;

    @Column(name = "allowed", nullable = false)
    private boolean allowed;

    @Column(name = "quota_monthly")
    private Integer quotaMonthly;

    @Column(name = "quota_total")
    private Integer quotaTotal;

    @Column(name = "max_items")
    private Integer maxItems;

    @Enumerated(EnumType.STRING)
    @Column(name = "reset_period", length = 30)
    private EntitlementResetPeriod resetPeriod;

    protected PlanEntitlement() {
    }

    public boolean hasUnlimitedMonthlyQuota() {
        return quotaMonthly == null;
    }
}
