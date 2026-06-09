package com.trajectiv.bll.exceptions;

import org.springframework.http.HttpStatus;

public enum BusinessErrorCode {

    INVALID_AUTHENTICATED_USER_CLAIMS(HttpStatus.UNAUTHORIZED),

    USER_PROFILE_NOT_INITIALIZED(HttpStatus.INTERNAL_SERVER_ERROR),
    USER_SUBSCRIPTION_NOT_INITIALIZED(HttpStatus.INTERNAL_SERVER_ERROR),
    USER_AI_CREDIT_WALLET_NOT_INITIALIZED(HttpStatus.INTERNAL_SERVER_ERROR),

    ONBOARDING_REQUIRED_FIELDS_MISSING(HttpStatus.BAD_REQUEST),

    USER_FILE_NOT_FOUND(HttpStatus.NOT_FOUND),
    EMPTY_FILE(HttpStatus.BAD_REQUEST),
    FILE_TOO_LARGE(HttpStatus.BAD_REQUEST),
    UNSUPPORTED_FILE_TYPE(HttpStatus.BAD_REQUEST),

    SUBSCRIPTION_REQUIRED(HttpStatus.FORBIDDEN),
    AI_CREDITS_EXHAUSTED(HttpStatus.FORBIDDEN);

    private final HttpStatus httpStatus;

    BusinessErrorCode(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public HttpStatus httpStatus() {
        return httpStatus;
    }
}
