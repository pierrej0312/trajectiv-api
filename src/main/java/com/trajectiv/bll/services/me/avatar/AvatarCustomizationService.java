package com.trajectiv.bll.services.me.avatar;

import com.trajectiv.bll.dto.me.avatar.AvatarCustomizationBllDto;
import com.trajectiv.bll.dto.me.avatar.CreateAvatarCustomizationBllCommand;
import com.trajectiv.bll.dto.me.avatar.PatchAvatarCustomizationBllCommand;
import org.springframework.security.core.Authentication;

public interface AvatarCustomizationService {

    AvatarCustomizationBllDto createCurrentUserAvatarCustomization(
            Authentication authentication,
            CreateAvatarCustomizationBllCommand command
    );

    AvatarCustomizationBllDto getCurrentUserAvatarCustomization(Authentication authentication);

    AvatarCustomizationBllDto patchCurrentUserAvatarCustomization(
            Authentication authentication,
            PatchAvatarCustomizationBllCommand command
    );

    void deleteCurrentUserAvatarCustomization(Authentication authentication);
}
