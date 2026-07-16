package com.trajectiv.bll.exceptions;

public class InvalidOrganizationInvitationException
        extends BusinessException {

    public InvalidOrganizationInvitationException(
            BusinessErrorCode errorCode,
            String message
    ) {
        super(errorCode, message);
    }
}