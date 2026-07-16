package com.trajectiv.dl.repositories.billing;

import com.trajectiv.dl.entities.billing.Plan;
import com.trajectiv.dl.enums.billing.PlanAudience;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlanRepository
        extends JpaRepository<Plan, UUID> {

    Optional<Plan> findByCode(
            String code
    );

    Optional<Plan> findByCodeAndActiveTrue(
            String code
    );

    List<Plan> findAllByAudienceAndActiveTrueOrderByCodeAsc(
            PlanAudience audience
    );

    boolean existsByCode(
            String code
    );
}