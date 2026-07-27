package com.trajectiv.bll.exceptions;

public class InvalidOrganizationMemberRoleChangeException
        extends BusinessException {

    public InvalidOrganizationMemberRoleChangeException(
            BusinessErrorCode errorCode,
            String message
    ) {
        super(errorCode, message);
    }
}