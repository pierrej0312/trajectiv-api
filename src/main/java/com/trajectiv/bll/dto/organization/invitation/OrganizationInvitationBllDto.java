package com.trajectiv.bll.dto.organization.invitation;

import com.trajectiv.dl.enums.organization.OrganizationInvitationStatus;
import com.trajectiv.dl.enums.organization.OrganizationRole;

import java.time.Instant;
import java.util.UUID;

public record OrganizationInvitationBllDto(
        UUID id,
        UUID organizationId,
        String organizationName,
        String email,
        OrganizationRole role,
        OrganizationInvitationStatus status,
        UUID invitedByUserId,
        String inviterDisplayName,
        Instant expiresAt,
        Instant acceptedAt,
        Instant revokedAt,
        Instant createdAt,
        Instant updatedAt
) {
}