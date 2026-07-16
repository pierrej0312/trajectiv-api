package com.trajectiv.dl.entities.credits;

import com.trajectiv.dl.entities.organization.Organization;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Getter
@Entity
@Table(
        name = "organization_ai_credit_wallets",
        indexes = {
                @Index(
                        name = "idx_organization_ai_wallets_organization_id",
                        columnList = "organization_id"
                ),
                @Index(
                        name = "idx_organization_ai_wallets_period",
                        columnList = "period_start, period_end"
                )
        }
)
@ToString(exclude = "organization")
public class OrganizationAiCreditWallet {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "organization_id",
            nullable = false,
            unique = true,
            updatable = false
    )
    private Organization organization;

    @Column(
            name = "monthly_limit",
            nullable = false
    )
    private int monthlyLimit;

    @Column(
            name = "used_this_period",
            nullable = false
    )
    private int usedThisPeriod;

    @Column(
            name = "remaining",
            nullable = false
    )
    private int remaining;

    @Column(
            name = "period_start",
            nullable = false
    )
    private LocalDate periodStart;

    @Column(
            name = "period_end",
            nullable = false
    )
    private LocalDate periodEnd;

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

    /**
     * Important pour un wallet partagé entre plusieurs membres.
     */
    @Version
    @Column(
            name = "version",
            nullable = false
    )
    private long version;

    protected OrganizationAiCreditWallet() {
    }

    private OrganizationAiCreditWallet(
            Organization organization,
            int monthlyLimit,
            int usedThisPeriod,
            LocalDate periodStart,
            LocalDate periodEnd
    ) {
        this.organization =
                Objects.requireNonNull(
                        organization,
                        "Organization cannot be null."
                );

        validateMonthlyLimit(monthlyLimit);
        validatePeriod(periodStart, periodEnd);
        validateUsedCredits(
                usedThisPeriod,
                monthlyLimit
        );

        this.monthlyLimit = monthlyLimit;
        this.usedThisPeriod = usedThisPeriod;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;

        recalculateRemaining();
    }

    public static OrganizationAiCreditWallet create(
            Organization organization,
            int monthlyLimit,
            LocalDate periodStart,
            LocalDate periodEnd
    ) {
        return new OrganizationAiCreditWallet(
                organization,
                monthlyLimit,
                0,
                periodStart,
                periodEnd
        );
    }

    public static OrganizationAiCreditWallet createMonthly(
            Organization organization,
            int monthlyLimit,
            LocalDate referenceDate
    ) {
        requireDate(
                referenceDate,
                "referenceDate"
        );

        LocalDate periodStart =
                referenceDate.withDayOfMonth(1);

        LocalDate periodEnd =
                periodStart
                        .plusMonths(1)
                        .minusDays(1);

        return create(
                organization,
                monthlyLimit,
                periodStart,
                periodEnd
        );
    }

    public void consume(
            int amount,
            LocalDate today
    ) {
        requireDate(today, "today");

        if (amount <= 0) {
            throw new IllegalArgumentException(
                    "Credit amount must be strictly positive."
            );
        }

        ensureDateInsideCurrentPeriod(today);

        if (amount > remaining) {
            throw new IllegalStateException(
                    "Not enough organization AI credits remaining."
            );
        }

        usedThisPeriod =
                Math.addExact(
                        usedThisPeriod,
                        amount
                );

        recalculateRemaining();
    }

    public boolean renewPeriodIfExpired(
            LocalDate today,
            int newMonthlyLimit
    ) {
        requireDate(today, "today");

        if (!isPeriodExpired(today)) {
            return false;
        }

        LocalDate newPeriodStart =
                today.withDayOfMonth(1);

        LocalDate newPeriodEnd =
                newPeriodStart
                        .plusMonths(1)
                        .minusDays(1);

        resetPeriod(
                newMonthlyLimit,
                newPeriodStart,
                newPeriodEnd
        );

        return true;
    }

    public void resetPeriod(
            int monthlyLimit,
            LocalDate periodStart,
            LocalDate periodEnd
    ) {
        validateMonthlyLimit(monthlyLimit);
        validatePeriod(
                periodStart,
                periodEnd
        );

        this.monthlyLimit = monthlyLimit;
        this.usedThisPeriod = 0;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;

        recalculateRemaining();
    }

    public void updateMonthlyLimit(
            int monthlyLimit
    ) {
        validateMonthlyLimit(monthlyLimit);

        if (monthlyLimit < usedThisPeriod) {
            throw new IllegalArgumentException(
                    "Monthly limit cannot be lower than already used credits."
            );
        }

        this.monthlyLimit = monthlyLimit;

        recalculateRemaining();
    }

    public boolean isPeriodExpired(
            LocalDate today
    ) {
        requireDate(today, "today");

        return today.isAfter(periodEnd);
    }

    public LocalDate getNextRenewalDate() {
        return periodEnd.plusDays(1);
    }

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();

        createdAt = now;
        updatedAt = now;

        validateCurrentState();
        recalculateRemaining();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();

        validateCurrentState();
        recalculateRemaining();
    }

    private void ensureDateInsideCurrentPeriod(
            LocalDate date
    ) {
        if (
                date.isBefore(periodStart)
                        || date.isAfter(periodEnd)
        ) {
            throw new IllegalStateException(
                    "Cannot consume credits outside the active period."
            );
        }
    }

    private void validateCurrentState() {
        if (organization == null) {
            throw new IllegalStateException(
                    "Organization cannot be null."
            );
        }

        validateMonthlyLimit(monthlyLimit);
        validatePeriod(
                periodStart,
                periodEnd
        );
        validateUsedCredits(
                usedThisPeriod,
                monthlyLimit
        );
    }

    private static void validateMonthlyLimit(
            int monthlyLimit
    ) {
        if (monthlyLimit < 0) {
            throw new IllegalArgumentException(
                    "Monthly limit cannot be negative."
            );
        }
    }

    private static void validateUsedCredits(
            int usedCredits,
            int monthlyLimit
    ) {
        if (usedCredits < 0) {
            throw new IllegalArgumentException(
                    "Used credits cannot be negative."
            );
        }

        if (usedCredits > monthlyLimit) {
            throw new IllegalArgumentException(
                    "Used credits cannot exceed the monthly limit."
            );
        }
    }

    private static void validatePeriod(
            LocalDate periodStart,
            LocalDate periodEnd
    ) {
        requireDate(
                periodStart,
                "periodStart"
        );

        requireDate(
                periodEnd,
                "periodEnd"
        );

        if (periodEnd.isBefore(periodStart)) {
            throw new IllegalArgumentException(
                    "Period end cannot precede period start."
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
        validateUsedCredits(
                usedThisPeriod,
                monthlyLimit
        );

        remaining =
                monthlyLimit -
                        usedThisPeriod;
    }
}