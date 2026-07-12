package com.trajectiv.api.mappers;

import com.trajectiv.api.dto.me.avatar.AvatarCustomizationResponseApiDto;
import com.trajectiv.api.dto.me.avatar.CreateAvatarCustomizationRequestApiDto;
import com.trajectiv.api.dto.me.avatar.PatchAvatarCustomizationRequestApiDto;
import com.trajectiv.bll.dto.me.avatar.AvatarCustomizationBllDto;
import com.trajectiv.bll.dto.me.avatar.CreateAvatarCustomizationBllCommand;
import com.trajectiv.bll.dto.me.avatar.PatchAvatarCustomizationBllCommand;
import org.springframework.stereotype.Component;

@Component
public class AvatarCustomizationApiMapper {
    public CreateAvatarCustomizationBllCommand toBllCommand(CreateAvatarCustomizationRequestApiDto request) {
        return new CreateAvatarCustomizationBllCommand(
                request.bodyType(),
                request.skinTone(),
                request.skinIntensity(),
                request.hairStyle(),
                request.hairColor(),
                request.beardStyle(),
                request.beardColor(),
                request.topStyle(),
                request.bottomStyle()
        );
    }

    public PatchAvatarCustomizationBllCommand toBllCommand(PatchAvatarCustomizationRequestApiDto request) {
        return new PatchAvatarCustomizationBllCommand(
                request.bodyType(),
                request.skinTone(),
                request.skinIntensity(),
                request.hairStyle(),
                request.hairColor(),
                request.beardStyle(),
                request.beardColor(),
                request.topStyle(),
                request.bottomStyle()
        );
    }

    public AvatarCustomizationResponseApiDto toApiDto(AvatarCustomizationBllDto dto) {
        return new AvatarCustomizationResponseApiDto(
                dto.id(),
                dto.bodyType(),
                dto.skinTone(),
                dto.skinIntensity(),
                dto.hairStyle(),
                dto.hairColor(),
                dto.beardStyle(),
                dto.beardColor(),
                dto.topStyle(),
                dto.bottomStyle(),
                dto.sourcePhotoFileId(),
                dto.sourcePhotoUrl(),
                dto.faceTextureFileId(),
                dto.faceTextureUrl()
        );
    }
}
