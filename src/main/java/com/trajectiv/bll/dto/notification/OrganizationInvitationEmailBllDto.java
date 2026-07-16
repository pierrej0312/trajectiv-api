package com.trajectiv.bll.dto.notification;

import java.time.Instant;

public record OrganizationInvitationEmailBllDto(
        String recipientEmail,
        String organizationName,
        String inviterDisplayName,
        String roleLabel,
        String acceptanceUrl,
        Instant expiresAt
) {
}