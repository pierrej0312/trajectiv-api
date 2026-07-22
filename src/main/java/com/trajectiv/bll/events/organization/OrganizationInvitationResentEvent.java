package com.trajectiv.bll.events.organization;

import com.trajectiv.dl.enums.organization.OrganizationRole;

import java.util.UUID;

public record OrganizationInvitationResentEvent(
        UUID organizationId,
        UUID actorUserId,
        UUID previousInvitationId,
        UUID newInvitationId,
        String invitedEmail,
        OrganizationRole role
) {
}
