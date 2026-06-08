package com.trajectiv.dl.entities;

import com.trajectiv.dl.enums.SubscriptionPlan;
import com.trajectiv.dl.enums.SubscriptionStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@Table(
        name = "subscriptions",
        indexes = {
                @Index(name = "idx_subscriptions_user_id", columnList = "user_id"),
                @Index(name = "idx_subscriptions_plan", columnList = "plan"),
                @Index(name = "idx_subscriptions_status", columnList = "status")
        }
)
@ToString(exclude = {"user"})
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true, updatable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "plan", nullable = false, length = 40)
    private SubscriptionPlan plan = SubscriptionPlan.FREE;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 40)
    private SubscriptionStatus status = SubscriptionStatus.ACTIVE;

    @Column(name = "current_period_start")
    private Instant currentPeriodStart;

    @Column(name = "current_period_end")
    private Instant currentPeriodEnd;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Subscription() {
    }

    private Subscription(User user, SubscriptionPlan plan, SubscriptionStatus status) {
        this.user = user;
        this.plan = plan;
        this.status = status;
    }

    public static Subscription createFree(User user) {
        return new Subscription(
                user,
                SubscriptionPlan.FREE,
                SubscriptionStatus.ACTIVE
        );
    }

    public void activatePremium(Instant currentPeriodStart, Instant currentPeriodEnd) {
        this.plan = SubscriptionPlan.PREMIUM;
        this.status = SubscriptionStatus.ACTIVE;
        this.currentPeriodStart = currentPeriodStart;
        this.currentPeriodEnd = currentPeriodEnd;
    }

    public void markPastDue() {
        this.status = SubscriptionStatus.PAST_DUE;
    }

    public void cancel() {
        this.status = SubscriptionStatus.CANCELED;
    }

    public void expire() {
        this.status = SubscriptionStatus.EXPIRED;
    }

    public void startTrial(Instant currentPeriodStart, Instant currentPeriodEnd) {
        this.plan = SubscriptionPlan.PREMIUM;
        this.status = SubscriptionStatus.TRIALING;
        this.currentPeriodStart = currentPeriodStart;
        this.currentPeriodEnd = currentPeriodEnd;
    }

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;

        if (this.plan == null) {
            this.plan = SubscriptionPlan.FREE;
        }

        if (this.status == null) {
            this.status = SubscriptionStatus.ACTIVE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }
}