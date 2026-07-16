package com.trajectiv.bll.services.access;

import com.trajectiv.bll.dto.access.OrganizationPermission;
import com.trajectiv.bll.exceptions.OrganizationAccessDeniedException;
import com.trajectiv.bll.exceptions.OrganizationNotActiveException;
import com.trajectiv.dl.entities.organization.OrganizationMember;
import com.trajectiv.dl.enums.organization.OrganizationMemberStatus;
import com.trajectiv.dl.repositories.organization.OrganizationMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrganizationAccessServiceImpl
        implements OrganizationAccessService {

    private final OrganizationMemberRepository memberRepository;

    private final OrganizationPermissionResolver
            permissionResolver;

    @Override
    public OrganizationMember getActiveMembership(
            UUID userId,
            UUID organizationId
    ) {
        requireIdentifiers(
                userId,
                organizationId
        );

        OrganizationMember membership =
                memberRepository
                        .findByUserIdAndOrganizationIdAndStatus(
                                userId,
                                organizationId,
                                OrganizationMemberStatus.ACTIVE
                        )
                        .orElseThrow(
                                () ->
                                        new OrganizationAccessDeniedException(
                                                userId,
                                                organizationId
                                        )
                        );

        if (!membership.getOrganization().isActive()) {
            throw new OrganizationNotActiveException(
                    organizationId
            );
        }

        return membership;
    }

    @Override
    public Set<OrganizationPermission> resolvePermissions(
            UUID userId,
            UUID organizationId
    ) {
        OrganizationMember membership =
                getActiveMembership(
                        userId,
                        organizationId
                );

        return permissionResolver.resolve(
                membership.getRole()
        );
    }

    @Override
    public boolean hasPermission(
            UUID userId,
            UUID organizationId,
            OrganizationPermission permission
    ) {
        Objects.requireNonNull(
                permission,
                "permission cannot be null."
        );

        return resolvePermissions(
                userId,
                organizationId
        ).contains(permission);
    }

    @Override
    public void requirePermission(
            UUID userId,
            UUID organizationId,
            OrganizationPermission permission
    ) {
        Objects.requireNonNull(
                permission,
                "permission cannot be null."
        );

        if (
                !hasPermission(
                        userId,
                        organizationId,
                        permission
                )
        ) {
            throw new OrganizationAccessDeniedException(
                    userId,
                    organizationId
            );
        }
    }

    private static void requireIdentifiers(
            UUID userId,
            UUID organizationId
    ) {
        Objects.requireNonNull(
                userId,
                "userId cannot be null."
        );

        Objects.requireNonNull(
                organizationId,
                "organizationId cannot be null."
        );
    }
}