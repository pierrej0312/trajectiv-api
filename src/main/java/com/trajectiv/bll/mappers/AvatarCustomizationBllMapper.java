package com.trajectiv.bll.mappers;

import com.trajectiv.bll.dto.me.avatar.AvatarCustomizationBllDto;
import com.trajectiv.dl.entities.UserAvatarCustomization;
import org.springframework.stereotype.Component;

@Component
public class AvatarCustomizationBllMapper {
    public AvatarCustomizationBllDto toBllDto(UserAvatarCustomization entity) {
        var sourcePhotoFile = entity.getSourcePhotoFile();
        var faceTextureFile = entity.getFaceTextureFile();

        return new AvatarCustomizationBllDto(
                entity.getId(),
                entity.getBodyType(),
                entity.getSkinTone(),
                entity.getSkinIntensity(),
                entity.getHairStyle(),
                entity.getHairColor(),
                entity.getBeardStyle(),
                entity.getBeardColor(),
                sourcePhotoFile != null ? sourcePhotoFile.getId() : null,
                sourcePhotoFile != null ? sourcePhotoFile.getPublicUrl() : null,
                faceTextureFile != null ? faceTextureFile.getId() : null,
                faceTextureFile != null ? faceTextureFile.getPublicUrl() : null,
                entity.getTopStyle(),
                entity.getBottomStyle()
        );
    }
}
