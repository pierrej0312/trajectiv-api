package com.trajectiv.bll.services.access;

import com.trajectiv.bll.dto.access.OrganizationPermission;
import com.trajectiv.dl.entities.organization.OrganizationMember;

import java.util.Set;
import java.util.UUID;

public interface OrganizationAccessService {

    OrganizationMember getActiveMembership(
            UUID userId,
            UUID organizationId
    );

    Set<OrganizationPermission> resolvePermissions(
            UUID userId,
            UUID organizationId
    );

    boolean hasPermission(
            UUID userId,
            UUID organizationId,
            OrganizationPermission permission
    );

    void requirePermission(
            UUID userId,
            UUID organizationId,
            OrganizationPermission permission
    );
}