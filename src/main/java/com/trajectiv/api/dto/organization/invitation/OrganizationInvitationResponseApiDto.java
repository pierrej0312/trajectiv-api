package com.trajectiv.api.dto.organization.invitation;

import com.trajectiv.api.dto.organization.OrganizationRoleApiDto;

import java.time.Instant;
import java.util.UUID;

public record OrganizationInvitationResponseApiDto(
        UUID id,
        UUID organizationId,
        String organizationName,
        String email,
        OrganizationRoleApiDto role,
        OrganizationInvitationStatusApiDto status,
        UUID invitedByUserId,
        String inviterDisplayName,
        Instant expiresAt,
        Instant acceptedAt,
        Instant revokedAt,
        Instant createdAt,
        Instant updatedAt
) {
}