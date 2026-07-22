package com.trajectiv.bll.events.organization.member;

import com.trajectiv.dl.enums.organization.OrganizationRole;

import java.util.UUID;

public record OrganizationMemberRoleChangedEvent(
        UUID organizationId,
        UUID actorUserId,
        UUID memberId,
        UUID targetUserId,
        OrganizationRole previousRole,
        OrganizationRole newRole
) {
}
