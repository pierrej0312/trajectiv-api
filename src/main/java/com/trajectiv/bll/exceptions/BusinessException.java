package com.trajectiv.bll.exceptions;

import lombok.Getter;

@Getter
public abstract class BusinessException extends RuntimeException {

    private static final String DEFAULT_INTERNAL_MESSAGE =
            "An unexpected internal error occurred.";

    private final BusinessErrorCode errorCode;
    private final boolean publicMessageSafe;

    protected BusinessException(
            BusinessErrorCode errorCode,
            String message
    ) {
        this(
                errorCode,
                message,
                null,
                errorCode.httpStatus().is4xxClientError()
        );
    }

    protected BusinessException(
            BusinessErrorCode errorCode,
            String message,
            Throwable cause
    ) {
        this(
                errorCode,
                message,
                cause,
                errorCode.httpStatus().is4xxClientError()
        );
    }

    protected BusinessException(
            BusinessErrorCode errorCode,
            String message,
            boolean publicMessageSafe
    ) {
        this(errorCode, message, null, publicMessageSafe);
    }

    protected BusinessException(
            BusinessErrorCode errorCode,
            String message,
            Throwable cause,
            boolean publicMessageSafe
    ) {
        super(message, cause);
        this.errorCode = errorCode;
        this.publicMessageSafe = publicMessageSafe;
    }

    public String getPublicMessage() {
        return publicMessageSafe
                ? getMessage()
                : DEFAULT_INTERNAL_MESSAGE;
    }
}