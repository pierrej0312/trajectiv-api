package com.trajectiv.bll.dto.me;

import com.trajectiv.dl.enums.UserStatus;

import java.util.UUID;

public record UserBllDto(
        UUID id,
        String keycloakSubject,
        String email,
        boolean emailVerified,
        String firstName,
        String lastName,
        String displayName,
        UserStatus status
) {
}
