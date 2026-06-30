package com.trajectiv.api.dto.me.avatar;

import java.util.UUID;

public record MeAvatarApiDto(
        UUID fileId,
        String avatarUrl
) {
}
