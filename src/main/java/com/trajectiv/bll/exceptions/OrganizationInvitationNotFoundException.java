package com.trajectiv.bll.exceptions;

import java.util.UUID;

public class OrganizationInvitationNotFoundException
        extends BusinessException {

    public OrganizationInvitationNotFoundException(
            UUID invitationId
    ) {
        super(
                BusinessErrorCode
                        .ORGANIZATION_INVITATION_NOT_FOUND,
                "Organization invitation was not found: "
                        + invitationId + "."
        );
    }

    public OrganizationInvitationNotFoundException() {
        super(
                BusinessErrorCode
                        .ORGANIZATION_INVITATION_NOT_FOUND,
                "Organization invitation was not found."
        );
    }
}