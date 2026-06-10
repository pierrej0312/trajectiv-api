package com.trajectiv.bll.dto.storage;

import java.util.UUID;

public record StoredAvatarBllDto(
        UUID fileId,
        String avatarUrl
) {
}
