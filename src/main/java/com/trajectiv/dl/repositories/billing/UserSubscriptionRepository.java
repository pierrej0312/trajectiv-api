package com.trajectiv.dl.repositories.billing;

import com.trajectiv.dl.entities.billing.UserSubscription;
import com.trajectiv.dl.enums.billing.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserSubscriptionRepository
        extends JpaRepository<UserSubscription, UUID> {

    Optional<UserSubscription> findByUserId(
            UUID userId
    );

    boolean existsByUserId(
            UUID userId
    );

    long countByPlanCode(
            SubscriptionPlan planCode
    );
}