package com.trajectiv.bll.exceptions;


public class AvatarCustomizationAlreadyExistsException extends BusinessException {

    public AvatarCustomizationAlreadyExistsException() {
        super(
                BusinessErrorCode.AVATAR_CUSTOMIZATION_ALREADY_EXISTS,
                "Avatar customization already exists for current user."
        );
    }
}
