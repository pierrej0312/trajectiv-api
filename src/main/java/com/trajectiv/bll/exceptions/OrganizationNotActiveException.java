package com.trajectiv.bll.exceptions;

import java.util.UUID;

public class OrganizationNotActiveException
        extends BusinessException {

    public OrganizationNotActiveException(
            UUID organizationId
    ) {
        super(
                BusinessErrorCode.ORGANIZATION_NOT_ACTIVE,
                "Organization is not active: "
                        + organizationId + "."
        );
    }
}