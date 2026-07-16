package com.trajectiv.bll.dto.organization;

import com.trajectiv.dl.enums.organization.OrganizationStatus;

import java.time.Instant;
import java.util.UUID;

public record OrganizationBllDto(
        UUID id,
        String slug,
        String name,
        String avatarUrl,
        OrganizationStatus status,
        Instant createdAt,
        Instant updatedAt
) {
}