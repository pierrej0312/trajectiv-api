package com.trajectiv.api.dto.organization;

import jakarta.validation.constraints.Size;

public record UpdateOrganizationRequestApiDto(

        @Size(min = 1, max = 180)
        String name,

        @Size(max = 1000)
        String avatarUrl
) {
}