package com.trajectiv.bll.events.organization.member;

import com.trajectiv.dl.enums.organization.OrganizationMemberStatus;

import java.util.UUID;

public record OrganizationMemberStatusChangedEvent(
        UUID organizationId,
        UUID actorUserId,
        UUID memberId,
        UUID targetUserId,
        OrganizationMemberStatus previousStatus,
        OrganizationMemberStatus newStatus
) {
}
