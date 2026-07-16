package com.trajectiv.api.dto.organization;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateOrganizationRequestApiDto(

        @NotBlank
        @Size(min = 2, max = 120)
        @Pattern(
                regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$",
                message = "Slug must contain lowercase letters, numbers and single hyphens only."
        )
        String slug,

        @NotBlank
        @Size(min = 2, max = 180)
        String name
) {
}