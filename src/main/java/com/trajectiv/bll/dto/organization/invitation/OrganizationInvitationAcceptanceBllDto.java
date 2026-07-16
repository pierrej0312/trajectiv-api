package com.trajectiv.bll.dto.organization.invitation;

import com.trajectiv.dl.enums.organization.OrganizationRole;

import java.time.Instant;
import java.util.UUID;

public record OrganizationInvitationAcceptanceBllDto(
        UUID invitationId,
        UUID organizationId,
        UUID membershipId,
        OrganizationRole role,
        Instant acceptedAt
) {
}