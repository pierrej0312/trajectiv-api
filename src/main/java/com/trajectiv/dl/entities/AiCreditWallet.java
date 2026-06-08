package com.trajectiv.dl.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Entity
@Table(
        name = "ai_credit_wallets",
        indexes = {
                @Index(name = "idx_ai_credit_wallets_user_id", columnList = "user_id"),
                @Index(name = "idx_ai_credit_wallets_period", columnList = "period_start, period_end")
        }
)
@ToString(exclude = {"user"})
public class AiCreditWallet {

    private static final int DEFAULT_FREE_MONTHLY_LIMIT = 20;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true, updatable = false)
    private User user;

    @Column(name = "monthly_limit", nullable = false)
    private int monthlyLimit;

    @Column(name = "used_this_period", nullable = false)
    private int usedThisPeriod;

    @Column(name = "remaining", nullable = false)
    private int remaining;

    @Column(name = "period_start", nullable = false)
    private LocalDate periodStart;

    @Column(name = "period_end", nullable = false)
    private LocalDate periodEnd;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected AiCreditWallet() {
    }

    private AiCreditWallet(
            User user,
            int monthlyLimit,
            int usedThisPeriod,
            LocalDate periodStart,
            LocalDate periodEnd
    ) {
        this.user = user;
        this.monthlyLimit = Math.max(0, monthlyLimit);
        this.usedThisPeriod = Math.max(0, usedThisPeriod);
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        recalculateRemaining();
    }

    public static AiCreditWallet createFreeDefault(User user) {
        LocalDate periodStart = LocalDate.now().withDayOfMonth(1);
        LocalDate periodEnd = periodStart.plusMonths(1).minusDays(1);

        return new AiCreditWallet(
                user,
                DEFAULT_FREE_MONTHLY_LIMIT,
                0,
                periodStart,
                periodEnd
        );
    }

    public void consume(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Credit amount must be positive.");
        }

        if (amount > this.remaining) {
            throw new IllegalStateException("Not enough AI credits remaining.");
        }

        this.usedThisPeriod += amount;
        recalculateRemaining();
    }

    public void resetPeriod(int monthlyLimit, LocalDate periodStart, LocalDate periodEnd) {
        this.monthlyLimit = Math.max(0, monthlyLimit);
        this.usedThisPeriod = 0;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        recalculateRemaining();
    }

    public void updateMonthlyLimit(int monthlyLimit) {
        this.monthlyLimit = Math.max(0, monthlyLimit);
        recalculateRemaining();
    }

    private void recalculateRemaining() {
        this.remaining = Math.max(0, this.monthlyLimit - this.usedThisPeriod);
    }

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;

        if (this.periodStart == null) {
            this.periodStart = LocalDate.now().withDayOfMonth(1);
        }

        if (this.periodEnd == null) {
            this.periodEnd = this.periodStart.plusMonths(1).minusDays(1);
        }

        if (this.monthlyLimit < 0) {
            this.monthlyLimit = 0;
        }

        if (this.usedThisPeriod < 0) {
            this.usedThisPeriod = 0;
        }

        recalculateRemaining();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
        recalculateRemaining();
    }
}