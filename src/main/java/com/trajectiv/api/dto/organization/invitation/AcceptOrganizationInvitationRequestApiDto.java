package com.trajectiv.api.dto.organization.invitation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AcceptOrganizationInvitationRequestApiDto(

        @NotBlank
        @Size(max = 512)
        String token
) {
}