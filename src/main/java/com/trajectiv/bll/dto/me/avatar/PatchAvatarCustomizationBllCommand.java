package com.trajectiv.bll.dto.me.avatar;

import com.trajectiv.dl.enums.avatar.*;

public record PatchAvatarCustomizationBllCommand(
        AvatarBodyType bodyType,
        AvatarSkinTone skinTone,
        Short skinIntensity,
        AvatarHairStyle hairStyle,
        String hairColor,
        AvatarBeardStyle beardStyle,
        String beardColor,
        AvatarTopStyle topStyle,
        AvatarBottomStyle bottomStyle
) {
}
