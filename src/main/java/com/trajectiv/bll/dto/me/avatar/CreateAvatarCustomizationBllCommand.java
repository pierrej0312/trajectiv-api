package com.trajectiv.bll.dto.me.avatar;

import com.trajectiv.dl.enums.avatar.*;

public record CreateAvatarCustomizationBllCommand(
        AvatarBodyType bodyType,
        AvatarSkinTone skinTone,
        short skinIntensity,
        AvatarHairStyle hairStyle,
        String hairColor,
        AvatarBeardStyle beardStyle,
        String beardColor,
        AvatarTopStyle topStyle,
        AvatarBottomStyle bottomStyle
) {
}
