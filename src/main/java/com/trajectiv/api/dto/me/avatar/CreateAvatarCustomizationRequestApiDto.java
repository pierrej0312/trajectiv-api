package com.trajectiv.api.dto.me.avatar;

import com.trajectiv.dl.enums.AvatarBeardStyle;
import com.trajectiv.dl.enums.AvatarBodyType;
import com.trajectiv.dl.enums.AvatarHairStyle;
import com.trajectiv.dl.enums.AvatarSkinTone;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record CreateAvatarCustomizationRequestApiDto(
        @NotNull AvatarBodyType bodyType,
        @NotNull AvatarSkinTone skinTone,
        @Min(-2) @Max(2) short skinIntensity,
        @NotNull AvatarHairStyle hairStyle,
        @NotNull @Pattern(regexp = "^#[0-9A-Fa-f]{6}$") String hairColor,
        @NotNull AvatarBeardStyle beardStyle,
        @NotNull @Pattern(regexp = "^#[0-9A-Fa-f]{6}$") String beardColor
) {
}