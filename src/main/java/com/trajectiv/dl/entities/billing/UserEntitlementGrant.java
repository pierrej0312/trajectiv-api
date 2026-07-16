package com.trajectiv.dl.entities.billing;

import com.trajectiv.dl.entities.User;
import com.trajectiv.dl.enums.entitlement.EntitlementSource;
import com.trajectiv.dl.enums.FeatureKey;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@Table(
        name = "user_entitlement_grants",
        indexes = {
                @Index(
                        name = "idx_user_entitlement_grants_user_id",
                        columnList = "user_id"
                ),
                @Index(
                        name = "idx_user_entitlement_grants_user_feature",
                        columnList = "user_id, feature_key"
                ),
                @Index(
                        name = "idx_user_entitlement_grants_validity",
                        columnList = "valid_from, valid_until"
                ),
                @Index(
                        name = "idx_user_entitlement_grants_source",
                        columnList = "source"
                )
        }
)
public class UserEntitlementGrant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            updatable = false
    )
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "feature_key",
            nullable = false,
            length = 100
    )
    private FeatureKey featureKey;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "source",
            nullable = false,
            length = 40
    )
    private EntitlementSource source;

    @Column(name = "allowed")
    private Boolean allowed;

    @Column(name = "quota_monthly")
    private Integer quotaMonthly;

    @Column(name = "quota_total")
    private Integer quotaTotal;

    @Column(name = "max_items")
    private Integer maxItems;

    @Column(name = "valid_from")
    private Instant validFrom;

    @Column(name = "valid_until")
    private Instant validUntil;

    protected UserEntitlementGrant() {
    }

    public boolean isActiveAt(Instant now) {
        return (validFrom == null || !now.isBefore(validFrom))
                && (validUntil == null || now.isBefore(validUntil));
    }
}
