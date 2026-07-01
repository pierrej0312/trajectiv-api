package com.trajectiv.bll.exceptions;

public class UserNotFoundException extends BusinessException {

    public UserNotFoundException() {
        super(
                BusinessErrorCode.USER_NOT_FOUND,
                "Authenticated user was not found in Trajectiv database."
        );
    }
}
