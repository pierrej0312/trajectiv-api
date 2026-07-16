package com.trajectiv.bll.dto.me.workspace;

import com.trajectiv.bll.dto.access.OrganizationPermission;
import com.trajectiv.bll.dto.billing.EffectiveEntitlementBllDto;
import com.trajectiv.dl.enums.organization.OrganizationRole;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public record MeWorkspaceBllDto(
        String id,
        WorkspaceKindBllDto kind,
        String label,
        String avatarUrl,
        UUID organizationId,
        OrganizationRole organizationRole,
        Set<OrganizationPermission> permissions,
        Set<EffectiveEntitlementBllDto> entitlements,
        WorkspacePlanBllDto plan
) {

    public MeWorkspaceBllDto {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException(
                    "Workspace id cannot be blank."
            );
        }

        Objects.requireNonNull(
                kind,
                "Workspace kind cannot be null."
        );

        if (label == null || label.isBlank()) {
            throw new IllegalArgumentException(
                    "Workspace label cannot be blank."
            );
        }

        Objects.requireNonNull(
                plan,
                "Workspace plan cannot be null."
        );

        id = id.trim();
        label = label.trim();

        permissions = permissions == null
                ? Set.of()
                : Set.copyOf(permissions);

        entitlements = entitlements == null
                ? Set.of()
                : Set.copyOf(entitlements);

        validateScope(
                kind,
                organizationId,
                organizationRole,
                permissions
        );
    }

    private static void validateScope(
            WorkspaceKindBllDto kind,
            UUID organizationId,
            OrganizationRole organizationRole,
            Set<OrganizationPermission> permissions
    ) {
        switch (kind) {
            case PERSONAL -> {
                if (
                        organizationId != null ||
                                organizationRole != null
                ) {
                    throw new IllegalArgumentException(
                            "A personal workspace cannot reference an organization."
                    );
                }

                if (!permissions.isEmpty()) {
                    throw new IllegalArgumentException(
                            "A personal workspace cannot expose organization permissions."
                    );
                }
            }

            case ORGANIZATION -> {
                if (organizationId == null) {
                    throw new IllegalArgumentException(
                            "An organization workspace requires an organization id."
                    );
                }

                if (organizationRole == null) {
                    throw new IllegalArgumentException(
                            "An organization workspace requires an organization role."
                    );
                }
            }
        }
    }
}