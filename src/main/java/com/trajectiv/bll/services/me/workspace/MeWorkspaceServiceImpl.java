package com.trajectiv.bll.services.me.workspace;

import com.trajectiv.bll.dto.access.OrganizationPermission;
import com.trajectiv.bll.dto.billing.EffectiveEntitlementBllDto;
import com.trajectiv.bll.dto.me.workspace.MeWorkspaceBllDto;
import com.trajectiv.bll.dto.me.workspace.WorkspaceKindBllDto;
import com.trajectiv.bll.dto.me.workspace.WorkspacePlanBllDto;
import com.trajectiv.bll.services.access.OrganizationPermissionResolver;
import com.trajectiv.bll.services.entitlement.organization.OrganizationEntitlementService;
import com.trajectiv.bll.services.entitlement.user.UserEntitlementService;
import com.trajectiv.bll.services.subscription.WorkspacePlanResolver;
import com.trajectiv.bll.services.subscription.organization.OrganizationSubscriptionService;
import com.trajectiv.bll.services.subscription.user.UserSubscriptionService;
import com.trajectiv.dl.entities.billing.OrganizationSubscription;
import com.trajectiv.dl.entities.billing.UserSubscription;
import com.trajectiv.dl.entities.organization.Organization;
import com.trajectiv.dl.entities.organization.OrganizationMember;
import com.trajectiv.dl.enums.organization.OrganizationMemberStatus;
import com.trajectiv.dl.enums.organization.OrganizationStatus;
import com.trajectiv.dl.repositories.organization.OrganizationMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MeWorkspaceServiceImpl
        implements MeWorkspaceService {

    private static final String PERSONAL_WORKSPACE_ID =
            "personal";

    private static final String PERSONAL_WORKSPACE_LABEL =
            "Compte personnel";

    private static final String ORGANIZATION_WORKSPACE_PREFIX =
            "organization:";

    private final OrganizationMemberRepository
            organizationMemberRepository;

    private final UserSubscriptionService
            userSubscriptionService;

    private final OrganizationSubscriptionService
            organizationSubscriptionService;

    private final UserEntitlementService
            userEntitlementService;

    private final OrganizationEntitlementService
            organizationEntitlementService;

    private final OrganizationPermissionResolver
            permissionResolver;

    private final WorkspacePlanResolver
            workspacePlanResolver;

    @Override
    public List<MeWorkspaceBllDto> getWorkspaces(
            UUID userId
    ) {
        validateUserId(userId);

        MeWorkspaceBllDto personalWorkspace =
                buildPersonalWorkspace(userId);

        List<MeWorkspaceBllDto> organizationWorkspaces =
                organizationMemberRepository
                        .findAllByUserIdAndStatuses(
                                userId,
                                OrganizationMemberStatus.ACTIVE,
                                OrganizationStatus.ACTIVE
                        )
                        .stream()
                        .map(this::buildOrganizationWorkspace)
                        .toList();

        return Stream.concat(
                        Stream.of(personalWorkspace),
                        organizationWorkspaces.stream()
                )
                .toList();
    }

    private MeWorkspaceBllDto buildPersonalWorkspace(
            UUID userId
    ) {
        UserSubscription subscription =
                userSubscriptionService.getCurrent(
                        userId
                );

        Set<EffectiveEntitlementBllDto> entitlements =
                userEntitlementService.resolveForUser(
                        userId
                );

        return new MeWorkspaceBllDto(
                PERSONAL_WORKSPACE_ID,
                WorkspaceKindBllDto.PERSONAL,
                PERSONAL_WORKSPACE_LABEL,
                null,
                null,
                null,
                Set.of(),
                entitlements,
                toWorkspacePlan(subscription)
        );
    }

    private MeWorkspaceBllDto buildOrganizationWorkspace(
            OrganizationMember member
    ) {
        Organization organization =
                member.getOrganization();

        UUID organizationId =
                organization.getId();

        OrganizationSubscription subscription =
                organizationSubscriptionService.getCurrent(
                        organizationId
                );

        Set<OrganizationPermission> permissions =
                permissionResolver.resolve(
                        member.getRole()
                );

        Set<EffectiveEntitlementBllDto> entitlements =
                organizationEntitlementService
                        .resolveForOrganization(
                                organizationId
                        );

        return new MeWorkspaceBllDto(
                ORGANIZATION_WORKSPACE_PREFIX
                        + organizationId,
                WorkspaceKindBllDto.ORGANIZATION,
                organization.getName(),
                organization.getAvatarUrl(),
                organizationId,
                member.getRole(),
                permissions,
                entitlements,
                toWorkspacePlan(subscription)
        );
    }

    private WorkspacePlanBllDto toWorkspacePlan(
            UserSubscription subscription
    ) {
        return workspacePlanResolver.resolve(
                subscription.getPlanCode(),
                subscription.getStatus()
        );
    }

    private WorkspacePlanBllDto toWorkspacePlan(
            OrganizationSubscription subscription
    ) {
        return new WorkspacePlanBllDto(
                subscription.getPlanCode(),
                resolvePlanLabel(
                        subscription.getPlanCode()
                ),
                subscription.getStatus()
        );
    }

    private String resolvePlanLabel(
            String planCode
    ) {
        return switch (planCode) {
            case "FREE" ->
                    "Free";

            case "STARTER" ->
                    "Starter";

            case "PRO" ->
                    "Pro";

            case "ORGANIZATION_STARTER" ->
                    "Organization Starter";

            case "ORGANIZATION_FORMATION" ->
                    "Organization Formation";

            case "ORGANIZATION_CENTER" ->
                    "Organization Center";

            default ->
                    planCode;
        };
    }

    private void validateUserId(
            UUID userId
    ) {
        if (userId == null) {
            throw new IllegalArgumentException(
                    "userId cannot be null."
            );
        }
    }
}
