package com.trajectiv.bll.exceptions;

public class AvatarCustomizationNotFoundException extends BusinessException {

    public AvatarCustomizationNotFoundException() {
        super(
                BusinessErrorCode.AVATAR_CUSTOMIZATION_NOT_FOUND,
                "Avatar customization was not found for current user."
        );
    }
}
