package com.trajectiv.dl.entities;

import com.trajectiv.dl.enums.SubscriptionPlan;
import com.trajectiv.dl.enums.SubscriptionStatus;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
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
@EqualsAndHashCode
@ToString(exclude = {"user"})
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;


    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "plan", nullable = false, length = 40)
    private SubscriptionPlan plan = SubscriptionPlan.FREE;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 40)
    private SubscriptionStatus status = SubscriptionStatus.ACTIVE;


    @Setter
    @Column(name = "current_period_start")
    private Instant currentPeriodStart;


    @Setter
    @Column(name = "current_period_end")
    private Instant currentPeriodEnd;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

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