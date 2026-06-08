package com.trajectiv.dl.entities;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
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
@EqualsAndHashCode
@ToString(exclude = {"user"})
public class AiCreditWallet {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Setter
    @Column(name = "monthly_limit", nullable = false)
    private int monthlyLimit;

    @Setter
    @Column(name = "used_this_period", nullable = false)
    private int usedThisPeriod;

    @Setter
    @Column(name = "remaining", nullable = false)
    private int remaining;

    @Setter
    @Column(name = "period_start", nullable = false)
    private LocalDate periodStart;

    @Setter
    @Column(name = "period_end", nullable = false)
    private LocalDate periodEnd;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

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

        this.remaining = Math.max(0, this.monthlyLimit - this.usedThisPeriod);
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
        this.remaining = Math.max(0, this.monthlyLimit - this.usedThisPeriod);
    }
}