package com.trajectiv.dl.entities.credits;

import com.trajectiv.dl.entities.User;
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

    @Version
    @Column(name = "version", nullable = false)
    private long version;

    protected AiCreditWallet() {
    }

    private AiCreditWallet(
            User user,
            int monthlyLimit,
            int usedThisPeriod,
            LocalDate periodStart,
            LocalDate periodEnd
    ) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }

        validateMonthlyLimit(monthlyLimit);
        validatePeriod(periodStart, periodEnd);

        if (usedThisPeriod < 0 || usedThisPeriod > monthlyLimit) {
            throw new IllegalArgumentException(
                    "Used credits must be between zero and monthly limit."
            );
        }

        this.user = user;
        this.monthlyLimit = monthlyLimit;
        this.usedThisPeriod = usedThisPeriod;
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

    public boolean isPeriodExpired(LocalDate today) {
        requireDate(today, "today");

        return today.isAfter(periodEnd);
    }

    public boolean renewPeriodIfExpired(
            LocalDate today,
            int monthlyLimit
    ) {
        requireDate(today, "today");

        if (!isPeriodExpired(today)) {
            return false;
        }

        LocalDate newPeriodStart = today.withDayOfMonth(1);
        LocalDate newPeriodEnd = newPeriodStart
                .plusMonths(1)
                .minusDays(1);

        resetPeriod(
                monthlyLimit,
                newPeriodStart,
                newPeriodEnd
        );

        return true;
    }

    public void consume(int amount, LocalDate today) {
        requireDate(today, "today");

        if (amount <= 0) {
            throw new IllegalArgumentException(
                    "Credit amount must be strictly positive."
            );
        }

        if (today.isBefore(periodStart) || today.isAfter(periodEnd)) {
            throw new IllegalStateException(
                    "Cannot consume credits outside the active period."
            );
        }

        if (amount > remaining) {
            throw new IllegalStateException(
                    "Not enough AI credits remaining."
            );
        }

        usedThisPeriod = Math.addExact(usedThisPeriod, amount);
        recalculateRemaining();
    }

    private void resetPeriod(
            int monthlyLimit,
            LocalDate periodStart,
            LocalDate periodEnd
    ) {
        validateMonthlyLimit(monthlyLimit);
        validatePeriod(periodStart, periodEnd);

        this.monthlyLimit = monthlyLimit;
        this.usedThisPeriod = 0;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;

        recalculateRemaining();
    }

    public void updateMonthlyLimit(int monthlyLimit) {
        validateMonthlyLimit(monthlyLimit);

        if (monthlyLimit < usedThisPeriod) {
            throw new IllegalArgumentException(
                    "Monthly limit cannot be lower than already used credits."
            );
        }

        this.monthlyLimit = monthlyLimit;
        recalculateRemaining();
    }

    public LocalDate getNextRenewalDate() {
        return periodEnd.plusDays(1);
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

    private static void validateMonthlyLimit(int monthlyLimit) {
        if (monthlyLimit < 0) {
            throw new IllegalArgumentException(
                    "Monthly limit cannot be negative."
            );
        }
    }

    private static void validatePeriod(
            LocalDate periodStart,
            LocalDate periodEnd
    ) {
        requireDate(periodStart, "periodStart");
        requireDate(periodEnd, "periodEnd");

        if (periodEnd.isBefore(periodStart)) {
            throw new IllegalArgumentException(
                    "Period end cannot be before period start."
            );
        }
    }

    private static void requireDate(
            LocalDate date,
            String fieldName
    ) {
        if (date == null) {
            throw new IllegalArgumentException(
                    fieldName + " cannot be null."
            );
        }
    }

    private void recalculateRemaining() {
        if (usedThisPeriod < 0) {
            throw new IllegalStateException(
                    "Used credits cannot be negative."
            );
        }

        if (usedThisPeriod > monthlyLimit) {
            throw new IllegalStateException(
                    "Used credits cannot exceed the monthly limit."
            );
        }

        remaining = monthlyLimit - usedThisPeriod;
    }
}