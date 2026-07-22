package com.trajectiv.api.dto.organization.member;

import com.trajectiv.api.dto.organization.OrganizationRoleApiDto;

import java.time.Instant;
import java.util.UUID;

public record OrganizationMemberResponseApiDto(
        UUID id,
        UUID userId,
        String email,
        String firstName,
        String lastName,
        String displayName,
        OrganizationRoleApiDto role,
        OrganizationMemberStatusApiDto status,
        Instant joinedAt,
        Instant createdAt,
        Instant updatedAt
) {
}