package com.trajectiv.bll.dto.me.avatar;

import com.trajectiv.dl.enums.AvatarBeardStyle;
import com.trajectiv.dl.enums.AvatarBodyType;
import com.trajectiv.dl.enums.AvatarHairStyle;
import com.trajectiv.dl.enums.AvatarSkinTone;

public record PatchAvatarCustomizationBllCommand(
        AvatarBodyType bodyType,
        AvatarSkinTone skinTone,
        Short skinIntensity,
        AvatarHairStyle hairStyle,
        String hairColor,
        AvatarBeardStyle beardStyle,
        String beardColor
) {
}
