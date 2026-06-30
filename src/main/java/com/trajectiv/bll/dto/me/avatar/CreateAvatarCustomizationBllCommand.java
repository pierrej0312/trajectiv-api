package com.trajectiv.bll.dto.me.avatar;

import com.trajectiv.dl.enums.AvatarBeardStyle;
import com.trajectiv.dl.enums.AvatarBodyType;
import com.trajectiv.dl.enums.AvatarHairStyle;
import com.trajectiv.dl.enums.AvatarSkinTone;

public record CreateAvatarCustomizationBllCommand(
        AvatarBodyType bodyType,
        AvatarSkinTone skinTone,
        short skinIntensity,
        AvatarHairStyle hairStyle,
        String hairColor,
        AvatarBeardStyle beardStyle,
        String beardColor
) {
}
