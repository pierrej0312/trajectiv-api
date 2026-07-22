package com.trajectiv.api.mappers;

import com.trajectiv.api.dto.me.subscription.SubscriptionStatusApiDto;
import com.trajectiv.api.dto.me.workspace.MeWorkspaceApiDto;
import com.trajectiv.api.dto.me.workspace.WorkspaceKindApiDto;
import com.trajectiv.api.dto.me.workspace.WorkspacePlanApiDto;
import com.trajectiv.api.dto.organization.OrganizationPermissionApiDto;
import com.trajectiv.api.dto.organization.OrganizationRoleApiDto;
import com.trajectiv.bll.dto.access.OrganizationPermission;
import com.trajectiv.bll.dto.me.workspace.MeWorkspaceBllDto;
import com.trajectiv.bll.dto.me.workspace.WorkspaceKindBllDto;
import com.trajectiv.bll.dto.me.workspace.WorkspacePlanBllDto;
import com.trajectiv.dl.enums.organization.OrganizationRole;
import com.trajectiv.dl.enums.billing.SubscriptionStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MeWorkspaceApiMapper {

    private final EffectiveEntitlementApiMapper
            entitlementMapper;

    public List<MeWorkspaceApiDto> toApiDtos(
            List<MeWorkspaceBllDto> workspaces
    ) {
        if (workspaces == null || workspaces.isEmpty()) {
            return List.of();
        }

        return workspaces.stream()
                .map(this::toApiDto)
                .toList();
    }

    public MeWorkspaceApiDto toApiDto(
            MeWorkspaceBllDto workspace
    ) {
        return new MeWorkspaceApiDto(
                workspace.id(),
                mapKind(workspace.kind()),
                workspace.label(),
                workspace.avatarUrl(),
                workspace.organizationId(),
                mapRole(workspace.organizationRole()),
                mapPermissions(workspace.permissions()),
                workspace.entitlements()
                        .stream()
                        .map(entitlementMapper::toApiDto)
                        .collect(Collectors.toUnmodifiableSet()),
                mapPlan(workspace.plan())
        );
    }

    private WorkspaceKindApiDto mapKind(
            WorkspaceKindBllDto kind
    ) {
        return switch (kind) {
            case PERSONAL ->
                    WorkspaceKindApiDto.PERSONAL;

            case ORGANIZATION ->
                    WorkspaceKindApiDto.ORGANIZATION;
        };
    }

    private OrganizationRoleApiDto mapRole(
            OrganizationRole role
    ) {
        if (role == null) {
            return null;
        }

        return switch (role) {
            case ORGANIZATION_OWNER ->
                    OrganizationRoleApiDto.ORGANIZATION_OWNER;

            case ORGANIZATION_ADMIN ->
                    OrganizationRoleApiDto.ORGANIZATION_ADMIN;

            case RECRUITER ->
                    OrganizationRoleApiDto.RECRUITER;

            case COACH ->
                    OrganizationRoleApiDto.COACH;

            case TRAINER ->
                    OrganizationRoleApiDto.TRAINER;

            case LEARNER ->
                    OrganizationRoleApiDto.LEARNER;
        };
    }

    private Set<OrganizationPermissionApiDto>
    mapPermissions(
            Set<OrganizationPermission> permissions
    ) {
        if (permissions == null || permissions.isEmpty()) {
            return Set.of();
        }

        return permissions.stream()
                .map(this::mapPermission)
                .collect(Collectors.toUnmodifiableSet());
    }

    private OrganizationPermissionApiDto mapPermission(
            OrganizationPermission permission
    ) {
        return switch (permission) {
            case ORGANIZATION_READ ->
                    OrganizationPermissionApiDto.ORGANIZATION_READ;

            case ORGANIZATION_UPDATE ->
                    OrganizationPermissionApiDto.ORGANIZATION_UPDATE;

            case ORGANIZATION_ARCHIVE ->
                    OrganizationPermissionApiDto.ORGANIZATION_ARCHIVE;

            case MEMBER_READ ->
                    OrganizationPermissionApiDto.MEMBER_READ;

            case MEMBER_INVITE ->
                    OrganizationPermissionApiDto.MEMBER_INVITE;

            case MEMBER_UPDATE_ROLE ->
                    OrganizationPermissionApiDto.MEMBER_UPDATE_ROLE;

            case MEMBER_UPDATE_STATUS ->
                    OrganizationPermissionApiDto.MEMBER_UPDATE_STATUS;

            case MEMBER_SUSPEND ->
                    OrganizationPermissionApiDto.MEMBER_SUSPEND;

            case MEMBER_REMOVE ->
                    OrganizationPermissionApiDto.MEMBER_REMOVE;

            case COHORT_READ ->
                    OrganizationPermissionApiDto.COHORT_READ;

            case COHORT_MANAGE ->
                    OrganizationPermissionApiDto.COHORT_MANAGE;

            case LEARNER_READ ->
                    OrganizationPermissionApiDto.LEARNER_READ;

            case TRAINING_ASSIGN ->
                    OrganizationPermissionApiDto.TRAINING_ASSIGN;

            case REPORT_READ ->
                    OrganizationPermissionApiDto.REPORT_READ;
        };
    }

    private WorkspacePlanApiDto mapPlan(
            WorkspacePlanBllDto plan
    ) {
        if (plan == null) {
            return null;
        }

        return new WorkspacePlanApiDto(
                plan.code(),
                plan.label(),
                mapSubscriptionStatus(plan.status())
        );
    }

    private SubscriptionStatusApiDto
    mapSubscriptionStatus(
            SubscriptionStatus status
    ) {
        return switch (status) {
            case ACTIVE ->
                    SubscriptionStatusApiDto.ACTIVE;

            case TRIALING ->
                    SubscriptionStatusApiDto.TRIALING;

            case PAST_DUE ->
                    SubscriptionStatusApiDto.PAST_DUE;

            case CANCELED ->
                    SubscriptionStatusApiDto.CANCELED;

            case EXPIRED ->
                    SubscriptionStatusApiDto.EXPIRED;
        };
    }
}