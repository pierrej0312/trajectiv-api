package com.trajectiv.bll.exceptions;

public class OrganizationSlugAlreadyExistsException
        extends BusinessException {

    public OrganizationSlugAlreadyExistsException(
            String slug
    ) {
        super(
                BusinessErrorCode
                        .ORGANIZATION_SLUG_ALREADY_EXISTS,
                "An organization already exists with slug: "
                        + slug + "."
        );
    }
}