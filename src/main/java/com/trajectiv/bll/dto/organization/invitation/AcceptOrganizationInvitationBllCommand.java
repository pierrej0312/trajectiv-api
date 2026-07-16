package com.trajectiv.bll.dto.organization.invitation;

public record AcceptOrganizationInvitationBllCommand(
        String token
) {

    public AcceptOrganizationInvitationBllCommand {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException(
                    "Invitation token cannot be blank."
            );
        }

        token = token.trim();
    }
}