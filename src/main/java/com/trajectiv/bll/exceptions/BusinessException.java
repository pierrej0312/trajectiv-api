package com.trajectiv.bll.exceptions;

import lombok.Getter;

@Getter
public abstract class BusinessException extends RuntimeException {

    private final BusinessErrorCode errorCode;

    protected BusinessException(BusinessErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

}
