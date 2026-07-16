package com.trajectiv.api.mappers;

import com.trajectiv.api.dto.me.avatar.MeAvatarApiDto;
import com.trajectiv.bll.dto.storage.StoredAvatarBllDto;
import org.springframework.stereotype.Component;

@Component
public class MeAvatarApiMapper {

    public MeAvatarApiDto toApiDto(
            StoredAvatarBllDto avatar
    ) {
        return new MeAvatarApiDto(
                avatar.fileId(),
                avatar.avatarUrl()
        );
    }
}