package com.trajectiv.bll.dto.me.avatar;

import com.trajectiv.dl.enums.*;

import java.util.UUID;

public record AvatarCustomizationBllDto(
        UUID id,
        AvatarBodyType bodyType,
        AvatarSkinTone skinTone,
        short skinIntensity,
        AvatarHairStyle hairStyle,
        String hairColor,
        AvatarBeardStyle beardStyle,
        String beardColor,
        UUID sourcePhotoFileId,
        String sourcePhotoUrl,
        UUID faceTextureFileId,
        String faceTextureUrl,
        AvatarTopStyle topStyle,
        AvatarBottomStyle bottomStyle
) {
}
