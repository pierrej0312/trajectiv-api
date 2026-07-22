package com.trajectiv.bll.events.organization;

import com.trajectiv.dl.enums.organization.OrganizationRole;

import java.util.UUID;

public record OrganizationInvitationAcceptedEvent(
        UUID organizationId,
        UUID actorUserId,
        UUID invitationId,
        UUID membershipId,
        OrganizationRole role
) {
}
