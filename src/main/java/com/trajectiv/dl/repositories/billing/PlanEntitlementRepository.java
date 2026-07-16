package com.trajectiv.dl.repositories.billing;

import com.trajectiv.dl.entities.billing.PlanEntitlement;
import com.trajectiv.dl.enums.FeatureKey;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlanEntitlementRepository
        extends JpaRepository<PlanEntitlement, UUID> {

    List<PlanEntitlement> findAllByPlanId(
            UUID planId
    );

    @EntityGraph(attributePaths = {
            "plan"
    })
    List<PlanEntitlement> findAllByPlanCode(
            String planCode
    );

    Optional<PlanEntitlement>
    findByPlanIdAndFeatureKey(
            UUID planId,
            FeatureKey featureKey
    );

    Optional<PlanEntitlement>
    findByPlanCodeAndFeatureKey(
            String planCode,
            FeatureKey featureKey
    );

    boolean existsByPlanIdAndFeatureKey(
            UUID planId,
            FeatureKey featureKey
    );
}