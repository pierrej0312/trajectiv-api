package com.trajectiv.bll.exceptions;

public class InvalidOrganizationMemberRoleChangeException
        extends RuntimeException {

    private final BusinessErrorCode errorCode;

    public InvalidOrganizationMemberRoleChangeException(
            BusinessErrorCode errorCode,
            String message
    ) {
        super(message);
        this.errorCode = errorCode;
    }

    public BusinessErrorCode getErrorCode() {
        return errorCode;
    }
}