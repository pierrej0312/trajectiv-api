package com.trajectiv.bll.exceptions;

import java.util.UUID;

public class OrganizationNotFoundException
        extends BusinessException {

    public OrganizationNotFoundException(
            UUID organizationId
    ) {
        super(
                BusinessErrorCode.ORGANIZATION_NOT_FOUND,
                "Organization was not found: "
                        + organizationId + "."
        );
    }
}