package com.trajectiv.bll.exceptions;

import java.util.UUID;

public class OrganizationAccessDeniedException
        extends BusinessException {

    public OrganizationAccessDeniedException(
            UUID userId,
            UUID organizationId
    ) {
        super(
                BusinessErrorCode.ORGANIZATION_ACCESS_DENIED,
                "User "
                        + userId
                        + " is not allowed to access organization "
                        + organizationId
                        + "."
        );
    }
}