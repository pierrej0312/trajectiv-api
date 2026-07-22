package com.trajectiv.api.dto.organization.member;

import com.trajectiv.api.dto.organization.OrganizationRoleApiDto;
import jakarta.validation.constraints.NotNull;

public record UpdateOrganizationMemberRoleRequestApiDto(
        @NotNull
        OrganizationRoleApiDto role
) {
}