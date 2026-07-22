package com.trajectiv.bll.events.organization;

import com.trajectiv.dl.enums.organization.OrganizationRole;

import java.util.UUID;

public record OrganizationInvitationRevokedEvent(
        UUID organizationId,
        UUID actorUserId,
        UUID invitationId,
        String invitedEmail,
        OrganizationRole role
) {
}
