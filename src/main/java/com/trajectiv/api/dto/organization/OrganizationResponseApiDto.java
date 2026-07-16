package com.trajectiv.api.dto.organization;

import java.time.Instant;
import java.util.UUID;

public record OrganizationResponseApiDto(
        UUID id,
        String slug,
        String name,
        String avatarUrl,
        OrganizationStatusApiDto status,
        Instant createdAt,
        Instant updatedAt
) {
}