package com.trajectiv.bll.dto.organization;

public record CreateOrganizationBllCommand(
        String slug,
        String name
) {

    public CreateOrganizationBllCommand {
        if (slug == null || slug.isBlank()) {
            throw new IllegalArgumentException(
                    "Organization slug cannot be blank."
            );
        }

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Organization name cannot be blank."
            );
        }

        slug = slug.trim().toLowerCase();
        name = name.trim();
    }
}