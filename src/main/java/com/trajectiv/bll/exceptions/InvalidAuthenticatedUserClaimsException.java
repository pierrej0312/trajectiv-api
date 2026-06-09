package com.trajectiv.bll.exceptions;

public class InvalidAuthenticatedUserClaimsException extends BusinessException {

    public InvalidAuthenticatedUserClaimsException(String message) {
        super(BusinessErrorCode.INVALID_AUTHENTICATED_USER_CLAIMS, message);
    }
}
