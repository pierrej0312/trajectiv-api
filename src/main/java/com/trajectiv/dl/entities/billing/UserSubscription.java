package com.trajectiv.dl.entities.billing;

import com.trajectiv.dl.entities.User;
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
        name = "user_subscriptions",
        indexes = {
                @Index(
                        name = "idx_user_subscriptions_user_id",
                        columnList = "user_id"
                ),
                @Index(
                        name = "idx_user_subscriptions_plan_code",
                        columnList = "plan_code"
                ),
                @Index(
                        name = "idx_user_subscriptions_status",
                        columnList = "status"
                )
        }
)
@ToString(exclude = "user")
public class UserSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            unique = true,
            updatable = false
    )
    private User user;

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

    @Column(name = "trial_end")
    private Instant trialEnd;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private Instant createdAt;

    @Column(
            name = "updated_at",
            nullable = false
    )
    private Instant updatedAt;

    protected UserSubscription() {
    }

    private UserSubscription(
            User user,
            String planCode
    ) {
        this.user = Objects.requireNonNull(user);
        this.planCode = requirePlanCode(planCode);
        this.status = SubscriptionStatus.ACTIVE;
    }

    public static UserSubscription createFree(
            User user
    ) {
        return new UserSubscription(
                user,
                "FREE"
        );
    }

    public void activate(
            String planCode,
            String stripeSubscriptionId,
            String stripePriceId,
            Instant periodStart,
            Instant periodEnd
    ) {
        validatePeriod(periodStart, periodEnd);

        this.planCode = requirePlanCode(planCode);
        this.stripeSubscriptionId =
                normalizeNullable(stripeSubscriptionId);
        this.stripePriceId =
                normalizeNullable(stripePriceId);
        this.currentPeriodStart = periodStart;
        this.currentPeriodEnd = periodEnd;
        this.status = SubscriptionStatus.ACTIVE;
    }

    public void markPastDue() {
        this.status = SubscriptionStatus.PAST_DUE;
    }

    public void cancelAtPeriodEnd() {
        this.cancelAtPeriodEnd = true;
    }

    public void expire() {
        this.status = SubscriptionStatus.EXPIRED;
    }

    public boolean isUsable() {
        return status == SubscriptionStatus.ACTIVE
                || status == SubscriptionStatus.TRIALING;
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