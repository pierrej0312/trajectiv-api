package com.trajectiv.api.dto.me;

import java.util.UUID;

public record MeAvatarApiDto(
        UUID fileId,
        String avatarUrl
) {
}
