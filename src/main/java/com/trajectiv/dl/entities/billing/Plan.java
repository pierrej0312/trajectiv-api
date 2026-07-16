package com.trajectiv.dl.entities.billing;

import com.trajectiv.dl.enums.billing.PlanAudience;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.UUID;

@Getter
@Entity
@Table(
        name = "plans",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_plans_code",
                        columnNames = "code"
                )
        }
)
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(
            name = "code",
            nullable = false,
            length = 80,
            updatable = false
    )
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "audience",
            nullable = false,
            length = 30
    )
    private PlanAudience audience;

    @Column(name = "label", nullable = false)
    private String label;

    @Column(name = "active", nullable = false)
    private boolean active;

    protected Plan() {
    }

    public boolean supportsUserSubscription() {
        return audience == PlanAudience.B2C;
    }

    public boolean supportsOrganizationSubscription() {
        return audience == PlanAudience.B2B;
    }
}
