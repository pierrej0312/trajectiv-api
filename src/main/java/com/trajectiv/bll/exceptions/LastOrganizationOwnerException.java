package com.trajectiv.bll.exceptions;

import java.util.UUID;

public class LastOrganizationOwnerException
        extends BusinessException {

    public LastOrganizationOwnerException(UUID organizationId) {
        super(
                BusinessErrorCode.ORGANIZATION_LAST_OWNER_CANNOT_BE_DEMOTED,
                "The last active owner of organization %s cannot be demoted."
                        .formatted(organizationId)
        );
    }
}