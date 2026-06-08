package com.trajectiv.repositories;

import com.trajectiv.dl.entities.Subscription;
import com.trajectiv.dl.enums.SubscriptionPlan;
import com.trajectiv.dl.enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {

    Optional<Subscription> findByUserId(UUID userId);

    boolean existsByUserId(UUID userId);

    long countByPlan(SubscriptionPlan plan);

    long countByStatus(SubscriptionStatus status);
}
