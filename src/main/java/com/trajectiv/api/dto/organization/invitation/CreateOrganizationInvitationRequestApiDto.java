package com.trajectiv.api.dto.organization.invitation;

import com.trajectiv.api.dto.organization.OrganizationRoleApiDto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateOrganizationInvitationRequestApiDto(

        @NotBlank
        @Email
        @Size(max = 320)
        String email,

        @NotNull
        OrganizationRoleApiDto role
) {
}