package com.trajectiv.bll.events.organization;

import com.trajectiv.bll.dto.notification.OrganizationInvitationEmailBllDto;

public record OrganizationInvitationCreatedEvent(
        OrganizationInvitationEmailBllDto email
) {
}