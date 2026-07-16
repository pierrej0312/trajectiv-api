package com.trajectiv.api.dto.me.workspace;

import com.trajectiv.api.dto.me.EffectiveEntitlementApiDto;
import com.trajectiv.api.dto.organization.OrganizationPermissionApiDto;
import com.trajectiv.api.dto.organization.OrganizationRoleApiDto;

import java.util.Set;
import java.util.UUID;

public record MeWorkspaceApiDto(
        String id,
        WorkspaceKindApiDto kind,
        String label,
        String avatarUrl,
        UUID organizationId,
        OrganizationRoleApiDto organizationRole,
        Set<OrganizationPermissionApiDto> permissions,
        Set<EffectiveEntitlementApiDto> entitlements,
        WorkspacePlanApiDto plan
) {

    public MeWorkspaceApiDto {
        permissions = permissions == null
                ? Set.of()
                : Set.copyOf(permissions);

        entitlements = entitlements == null
                ? Set.of()
                : Set.copyOf(entitlements);
    }
}