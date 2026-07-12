package com.trajectiv.api.dto.me.avatar;

import com.trajectiv.dl.enums.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

public record PatchAvatarCustomizationRequestApiDto(
        AvatarBodyType bodyType,
        AvatarSkinTone skinTone,
        @Min(-2) @Max(2) Short skinIntensity,
        AvatarHairStyle hairStyle,
        @Pattern(regexp = "^#[0-9A-Fa-f]{6}$") String hairColor,
        AvatarBeardStyle beardStyle,
        @Pattern(regexp = "^#[0-9A-Fa-f]{6}$") String beardColor,
        AvatarTopStyle topStyle,
        AvatarBottomStyle bottomStyle
) {
}
