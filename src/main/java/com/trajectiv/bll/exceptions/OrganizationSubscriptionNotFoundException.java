package com.trajectiv.bll.exceptions;

import java.util.UUID;

public class OrganizationSubscriptionNotFoundException
        extends BusinessException {

    public OrganizationSubscriptionNotFoundException(
            UUID organizationId
    ) {
        super(
                BusinessErrorCode
                        .ORGANIZATION_SUBSCRIPTION_NOT_FOUND,
                "No subscription was found for organization "
                        + organizationId + "."
        );
    }
}