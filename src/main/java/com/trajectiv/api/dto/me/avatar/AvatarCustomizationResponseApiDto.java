package com.trajectiv.api.dto.me.avatar;

import com.trajectiv.dl.enums.AvatarBeardStyle;
import com.trajectiv.dl.enums.AvatarBodyType;
import com.trajectiv.dl.enums.AvatarHairStyle;
import com.trajectiv.dl.enums.AvatarSkinTone;

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
        UUID sourcePhotoFileId,
        String sourcePhotoUrl,
        UUID faceTextureFileId,
        String faceTextureUrl
) {
}
