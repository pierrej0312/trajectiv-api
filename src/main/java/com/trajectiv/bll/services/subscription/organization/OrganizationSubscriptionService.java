package com.trajectiv.bll.services.subscription.organization;

import com.trajectiv.dl.entities.billing.OrganizationSubscription;
import com.trajectiv.dl.entities.organization.Organization;

import java.util.UUID;

public interface OrganizationSubscriptionService {

    OrganizationSubscription getCurrent(
            UUID organizationId
    );

    OrganizationSubscription createStarterIfMissing(
            Organization organization
    );
}