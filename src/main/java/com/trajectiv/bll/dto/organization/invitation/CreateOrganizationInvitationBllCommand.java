package com.trajectiv.bll.dto.organization.invitation;


import com.trajectiv.dl.enums.organization.OrganizationRole;

import java.util.Objects;

public record CreateOrganizationInvitationBllCommand(
        String email,
        OrganizationRole role
) {

    public CreateOrganizationInvitationBllCommand {
        email = normalizeEmail(email);

        Objects.requireNonNull(
                role,
                "role cannot be null."
        );

        if (role == OrganizationRole.ORGANIZATION_OWNER) {
            throw new IllegalArgumentException(
                    "Organization ownership cannot be granted through an invitation."
            );
        }
    }

    private static String normalizeEmail(
            String value
    ) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "Invitation email cannot be blank."
            );
        }

        return value.trim().toLowerCase();
    }
}