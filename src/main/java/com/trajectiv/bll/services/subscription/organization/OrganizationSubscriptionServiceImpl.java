package com.trajectiv.bll.services.subscription.organization;

import com.trajectiv.bll.exceptions.OrganizationSubscriptionNotFoundException;
import com.trajectiv.dl.entities.billing.OrganizationSubscription;
import com.trajectiv.dl.entities.organization.Organization;
import com.trajectiv.dl.repositories.billing.OrganizationSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrganizationSubscriptionServiceImpl
        implements OrganizationSubscriptionService {

    private final OrganizationSubscriptionRepository repository;

    @Override
    @Transactional(readOnly = true)
    public OrganizationSubscription getCurrent(
            UUID organizationId
    ) {
        Objects.requireNonNull(
                organizationId,
                "organizationId cannot be null."
        );

        return repository
                .findByOrganizationId(
                        organizationId
                )
                .orElseThrow(
                        () ->
                                new OrganizationSubscriptionNotFoundException(
                                        organizationId
                                )
                );
    }

    @Override
    @Transactional
    public OrganizationSubscription createStarterIfMissing(
            Organization organization
    ) {
        Objects.requireNonNull(
                organization,
                "organization cannot be null."
        );

        return repository
                .findByOrganizationId(
                        organization.getId()
                )
                .orElseGet(
                        () -> repository.save(
                                OrganizationSubscription
                                        .createStarter(
                                                organization
                                        )
                        )
                );
    }
}