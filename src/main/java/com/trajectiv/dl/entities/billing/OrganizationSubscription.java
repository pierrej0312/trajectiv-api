package com.trajectiv.dl.entities.billing;

import com.trajectiv.dl.entities.organization.Organization;
import com.trajectiv.dl.enums.billing.SubscriptionStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Getter
@Entity
@Table(
        name = "organization_subscriptions",
        indexes = {
                @Index(
                        name = "idx_org_subscriptions_organization_id",
                        columnList = "organization_id"
                ),
                @Index(
                        name = "idx_org_subscriptions_plan_code",
                        columnList = "plan_code"
                ),
                @Index(
                        name = "idx_org_subscriptions_status",
                        columnList = "status"
                )
        }
)
@ToString(exclude = "organization")
public class OrganizationSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "organization_id",
            nullable = false,
            unique = true,
            updatable = false
    )
    private Organization organization;

    @Column(
            name = "plan_code",
            nullable = false,
            length = 80
    )
    private String planCode;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 40
    )
    private SubscriptionStatus status;

    @Column(name = "stripe_subscription_id", unique = true)
    private String stripeSubscriptionId;

    @Column(name = "stripe_price_id")
    private String stripePriceId;

    @Column(name = "current_period_start")
    private Instant currentPeriodStart;

    @Column(name = "current_period_end")
    private Instant currentPeriodEnd;

    @Column(name = "cancel_at_period_end", nullable = false)
    private boolean cancelAtPeriodEnd;

    @Column(name = "seat_limit")
    private Integer seatLimit;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected OrganizationSubscription() {
    }

    private OrganizationSubscription(
            Organization organization,
            String planCode
    ) {
        this.organization =
                Objects.requireNonNull(organization);

        this.planCode =
                requirePlanCode(planCode);

        this.status =
                SubscriptionStatus.ACTIVE;
    }

    public static OrganizationSubscription createStarter(
            Organization organization
    ) {
        return new OrganizationSubscription(
                organization,
                "ORGANIZATION_STARTER"
        );
    }

    public void activate(
            String planCode,
            String stripeSubscriptionId,
            String stripePriceId,
            Instant periodStart,
            Instant periodEnd,
            Integer seatLimit
    ) {
        validatePeriod(periodStart, periodEnd);
        validateSeatLimit(seatLimit);

        this.planCode =
                requireOrganizationPlanCode(planCode);

        this.stripeSubscriptionId =
                normalizeNullable(stripeSubscriptionId);

        this.stripePriceId =
                normalizeNullable(stripePriceId);

        this.currentPeriodStart = periodStart;
        this.currentPeriodEnd = periodEnd;
        this.seatLimit = seatLimit;
        this.status = SubscriptionStatus.ACTIVE;
    }

    public boolean canAddMember(
            long activeMemberCount
    ) {
        return seatLimit == null
                || activeMemberCount < seatLimit;
    }

    public void markPastDue() {
        status = SubscriptionStatus.PAST_DUE;
    }

    public void cancelAtPeriodEnd() {
        cancelAtPeriodEnd = true;
    }

    public void expire() {
        status = SubscriptionStatus.EXPIRED;
    }

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    private static String requireOrganizationPlanCode(
            String planCode
    ) {
        String normalized =
                requirePlanCode(planCode);

        if (!normalized.startsWith("ORGANIZATION_")) {
            throw new IllegalArgumentException(
                    "Organization subscription requires an organization plan."
            );
        }

        return normalized;
    }

    private static void validateSeatLimit(
            Integer seatLimit
    ) {
        if (seatLimit != null && seatLimit <= 0) {
            throw new IllegalArgumentException(
                    "Seat limit must be positive."
            );
        }
    }

    private static String requirePlanCode(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "Plan code cannot be blank."
            );
        }

        return value.trim().toUpperCase();
    }

    private static void validatePeriod(
            Instant start,
            Instant end
    ) {
        if (start == null || end == null) {
            throw new IllegalArgumentException(
                    "Subscription period cannot be null."
            );
        }

        if (end.isBefore(start)) {
            throw new IllegalArgumentException(
                    "Subscription period end cannot precede start."
            );
        }
    }

    private static String normalizeNullable(
            String value
    ) {
        return value == null || value.isBlank()
                ? null
                : value.trim();
    }
}
