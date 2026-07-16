package com.trajectiv.bll.dto.organization;

public record UpdateOrganizationBllCommand(
        String name,
        String avatarUrl
) {

    public UpdateOrganizationBllCommand {
        if (name != null) {
            name = name.trim();

            if (name.isEmpty()) {
                throw new IllegalArgumentException(
                        "Organization name cannot be blank."
                );
            }
        }

        if (avatarUrl != null) {
            avatarUrl = avatarUrl.trim();
        }

        if (name == null && avatarUrl == null) {
            throw new IllegalArgumentException(
                    "At least one organization property must be provided."
            );
        }
    }

    public boolean hasNameUpdate() {
        return name != null;
    }

    public boolean hasAvatarUpdate() {
        return avatarUrl != null;
    }
}