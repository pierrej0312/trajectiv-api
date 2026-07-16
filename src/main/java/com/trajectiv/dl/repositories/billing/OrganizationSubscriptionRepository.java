package com.trajectiv.dl.repositories.billing;

import com.trajectiv.dl.entities.billing.OrganizationSubscription;
import com.trajectiv.dl.enums.billing.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrganizationSubscriptionRepository
        extends JpaRepository<
        OrganizationSubscription,
        UUID
        > {

    Optional<OrganizationSubscription>
    findByOrganizationId(
            UUID organizationId
    );

    Optional<OrganizationSubscription>
    findByOrganizationIdAndStatus(
            UUID organizationId,
            SubscriptionStatus status
    );

    Optional<OrganizationSubscription>
    findByStripeSubscriptionId(
            String stripeSubscriptionId
    );

    boolean existsByOrganizationId(
            UUID organizationId
    );
}