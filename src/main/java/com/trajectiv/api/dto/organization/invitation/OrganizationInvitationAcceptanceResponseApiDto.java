package com.trajectiv.api.dto.organization.invitation;

import com.trajectiv.api.dto.organization.OrganizationRoleApiDto;

import java.time.Instant;
import java.util.UUID;

public record OrganizationInvitationAcceptanceResponseApiDto(
        UUID invitationId,
        UUID organizationId,
        UUID membershipId,
        OrganizationRoleApiDto role,
        Instant acceptedAt
) {
}