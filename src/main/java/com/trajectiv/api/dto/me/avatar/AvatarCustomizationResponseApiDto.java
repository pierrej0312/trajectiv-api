package com.trajectiv.api.dto.me.avatar;

import com.trajectiv.dl.enums.*;

import java.util.UUID;

public record AvatarCustomizationResponseApiDto(
        UUID id,
        AvatarBodyType bodyType,
        AvatarSkinTone skinTone,
        short skinIntensity,
        AvatarHairStyle hairStyle,
        String hairColor,
        AvatarBeardStyle beardStyle,
        String beardColor,
        AvatarTopStyle topStyle,
        AvatarBottomStyle bottomStyle,
        UUID sourcePhotoFileId,
        String sourcePhotoUrl,
        UUID faceTextureFileId,
        String faceTextureUrl
) {
}
