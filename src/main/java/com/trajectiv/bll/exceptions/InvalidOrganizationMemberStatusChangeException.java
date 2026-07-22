package com.trajectiv.bll.exceptions;

public class InvalidOrganizationMemberStatusChangeException
        extends BusinessException {

    public InvalidOrganizationMemberStatusChangeException(
            BusinessErrorCode errorCode,
            String message
    ) {
        super(
                errorCode,
                message
        );
    }
}