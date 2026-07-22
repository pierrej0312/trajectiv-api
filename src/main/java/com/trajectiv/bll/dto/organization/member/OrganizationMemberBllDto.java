package com.trajectiv.bll.dto.organization.member;

import com.trajectiv.dl.enums.organization.OrganizationMemberStatus;
import com.trajectiv.dl.enums.organization.OrganizationRole;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record OrganizationMemberBllDto(
        UUID id,
        UUID userId,
        String email,
        String firstName,
        String lastName,
        String displayName,
        OrganizationRole role,
        OrganizationMemberStatus status,
        Instant joinedAt,
        Instant createdAt,
        Instant updatedAt
) {

    public OrganizationMemberBllDto {
        Objects.requireNonNull(
                id,
                "Organization member id cannot be null."
        );

        Objects.requireNonNull(
                userId,
                "Organization member user id cannot be null."
        );

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                    "Organization member email cannot be blank."
            );
        }

        Objects.requireNonNull(
                role,
                "Organization member role cannot be null."
        );

        Objects.requireNonNull(
                status,
                "Organization member status cannot be null."
        );

        Objects.requireNonNull(
                createdAt,
                "Organization member creation date cannot be null."
        );

        Objects.requireNonNull(
                updatedAt,
                "Organization member update date cannot be null."
        );

        email = email.trim().toLowerCase();
        firstName = normalizeNullable(firstName);
        lastName = normalizeNullable(lastName);
        displayName = normalizeNullable(displayName);
    }

    private static String normalizeNullable(
            String value
    ) {
        return value == null || value.isBlank()
                ? null
                : value.trim();
    }
}