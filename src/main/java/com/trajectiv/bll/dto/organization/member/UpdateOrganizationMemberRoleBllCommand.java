package com.trajectiv.bll.dto.organization.member;

import com.trajectiv.dl.enums.organization.OrganizationRole;

import java.util.Objects;

public record UpdateOrganizationMemberRoleBllCommand(
        OrganizationRole role
) {

    public UpdateOrganizationMemberRoleBllCommand {
        Objects.requireNonNull(
                role,
                "Organization role cannot be null."
        );
    }
}