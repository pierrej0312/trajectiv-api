package com.trajectiv.bll.exceptions;

public class InvalidAvatarCustomizationException extends BusinessException {

    public InvalidAvatarCustomizationException(String message) {
        super(BusinessErrorCode.INVALID_AVATAR_CUSTOMIZATION, message);
    }
}
