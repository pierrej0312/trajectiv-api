package com.trajectiv.bll.exceptions;

import java.util.UUID;

public class UserContextInitializationException extends BusinessException {

    public UserContextInitializationException(
            BusinessErrorCode errorCode,
            UUID userId,
            String resourceName
    ) {
        super(
                errorCode,
                "User context resource '" + resourceName + "' was not initialized for user " + userId + "."
        );
    }
}