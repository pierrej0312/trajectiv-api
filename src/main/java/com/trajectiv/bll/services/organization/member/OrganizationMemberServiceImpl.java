package com.trajectiv.bll.services.organization.member;

import com.trajectiv.bll.dto.access.OrganizationPermission;
import com.trajectiv.bll.dto.organization.member.OrganizationMemberBllDto;
import com.trajectiv.bll.events.organization.member.OrganizationMemberRemovedEvent;
import com.trajectiv.bll.events.organization.member.OrganizationMemberRoleChangedEvent;
import com.trajectiv.bll.events.organization.member.OrganizationMemberStatusChangedEvent;
import com.trajectiv.bll.dto.organization.member.UpdateOrganizationMemberRoleBllCommand;
import com.trajectiv.bll.exceptions.OrganizationMemberNotFoundException;
import com.trajectiv.bll.mappers.organization.OrganizationMemberBllMapper;
import com.trajectiv.bll.services.access.OrganizationAccessService;
import com.trajectiv.bll.services.organization.member.policy.OrganizationMemberLifecyclePolicy;
import com.trajectiv.bll.services.organization.member.policy.OrganizationMemberRolePolicy;
import com.trajectiv.dl.entities.organization.OrganizationMember;
import com.trajectiv.dl.enums.organization.OrganizationMemberStatus;
import com.trajectiv.dl.enums.organization.OrganizationRole;
import com.trajectiv.dl.repositories.organization.OrganizationMemberRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrganizationMemberServiceImpl
        implements OrganizationMemberService {

    private static final Set<OrganizationMemberStatus>
            VISIBLE_MEMBER_STATUSES =
            Set.of(
                    OrganizationMemberStatus.ACTIVE,
                    OrganizationMemberStatus.SUSPENDED
            );

    private final OrganizationMemberRolePolicy
            organizationMemberRolePolicy;

    private final OrganizationMemberLifecyclePolicy
            organizationMemberLifecyclePolicy;

    private final OrganizationAccessService
            organizationAccessService;

    private final OrganizationMemberRepository
            organizationMemberRepository;

    private final OrganizationMemberBllMapper
            organizationMemberMapper;

    private final EntityManager entityManager;

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public List<OrganizationMemberBllDto> getMembers(
            UUID currentUserId,
            UUID organizationId
    ) {
        requireIdentifiers(
                currentUserId,
                organizationId
        );

        organizationAccessService.requirePermission(
                currentUserId,
                organizationId,
                OrganizationPermission.MEMBER_READ
        );

        return organizationMemberMapper.toDtos(
                organizationMemberRepository
                        .findAllByOrganization_IdAndStatusInOrderByJoinedAtAsc(
                                organizationId,
                                VISIBLE_MEMBER_STATUSES
                        )
        );
    }

    @Override
    @Transactional
    public OrganizationMemberBllDto changeRole(
            UUID currentUserId,
            UUID organizationId,
            UUID memberId,
            UpdateOrganizationMemberRoleBllCommand command
    ) {
        requireIdentifiers(
                currentUserId,
                organizationId,
                memberId
        );

        Objects.requireNonNull(
                command,
                "command cannot be null."
        );

        organizationAccessService.requirePermission(
                currentUserId,
                organizationId,
                OrganizationPermission.MEMBER_UPDATE_ROLE
        );

        OrganizationMember actor =
                getRequiredActorMembership(
                        currentUserId,
                        organizationId
                );

        OrganizationMember target =
                getRequiredMemberForUpdate(
                        memberId,
                        organizationId
                );

        List<OrganizationMember> activeOwners =
                requiresOwnerLock(
                        target,
                        command.role()
                )
                        ? lockActiveOwners(
                        organizationId
                )
                        : List.of();

        organizationMemberRolePolicy.validateChange(
                actor,
                target,
                command.role(),
                activeOwners
        );

        if (target.getRole() == command.role()) {
            return organizationMemberMapper.toDto(
                    target
            );
        }

        OrganizationRole previousRole =
                target.getRole();

        target.changeRole(
                command.role()
        );

        eventPublisher.publishEvent(
                new OrganizationMemberRoleChangedEvent(
                        organizationId,
                        currentUserId,
                        target.getId(),
                        target.getUser().getId(),
                        previousRole,
                        target.getRole()
                )
        );

        organizationMemberRepository.flush();

        entityManager.refresh(target);

        return organizationMemberMapper.toDto(
                target
        );
    }

    @Override
    @Transactional
    public OrganizationMemberBllDto suspend(
            UUID currentUserId,
            UUID organizationId,
            UUID memberId
    ) {
        requireIdentifiers(
                currentUserId,
                organizationId,
                memberId
        );

        organizationAccessService.requirePermission(
                currentUserId,
                organizationId,
                OrganizationPermission.MEMBER_UPDATE_STATUS
        );

        OrganizationMember actor =
                getRequiredActorMembership(
                        currentUserId,
                        organizationId
                );

        OrganizationMember target =
                getRequiredMemberForUpdate(
                        memberId,
                        organizationId
                );

        List<OrganizationMember> activeOwners =
                requiresActiveOwnerProtection(target)
                        ? lockActiveOwners(organizationId)
                        : List.of();

        organizationMemberLifecyclePolicy
                .validateSuspension(
                        actor,
                        target,
                        activeOwners
                );

        OrganizationMemberStatus previousStatus =
                target.getStatus();

        target.suspend();

        eventPublisher.publishEvent(
                new OrganizationMemberStatusChangedEvent(
                        organizationId,
                        currentUserId,
                        target.getId(),
                        target.getUser().getId(),
                        previousStatus,
                        target.getStatus()
                )
        );

        organizationMemberRepository.flush();

        return organizationMemberMapper.toDto(
                target
        );
    }

    @Override
    @Transactional
    public OrganizationMemberBllDto reactivate(
            UUID currentUserId,
            UUID organizationId,
            UUID memberId
    ) {
        requireIdentifiers(
                currentUserId,
                organizationId,
                memberId
        );

        organizationAccessService.requirePermission(
                currentUserId,
                organizationId,
                OrganizationPermission.MEMBER_UPDATE_STATUS
        );

        OrganizationMember actor =
                getRequiredActorMembership(
                        currentUserId,
                        organizationId
                );

        OrganizationMember target =
                getRequiredMemberForUpdate(
                        memberId,
                        organizationId
                );

        organizationMemberLifecyclePolicy
                .validateReactivation(
                        actor,
                        target
                );

        OrganizationMemberStatus previousStatus =
                target.getStatus();

        target.reactivate();

        eventPublisher.publishEvent(
                new OrganizationMemberStatusChangedEvent(
                        organizationId,
                        currentUserId,
                        target.getId(),
                        target.getUser().getId(),
                        previousStatus,
                        target.getStatus()
                )
        );
        organizationMemberRepository.flush();

        return organizationMemberMapper.toDto(
                target
        );
    }

    @Override
    @Transactional
    public void remove(
            UUID currentUserId,
            UUID organizationId,
            UUID memberId
    ) {
        requireIdentifiers(
                currentUserId,
                organizationId,
                memberId
        );

        organizationAccessService.requirePermission(
                currentUserId,
                organizationId,
                OrganizationPermission.MEMBER_REMOVE
        );

        OrganizationMember actor =
                getRequiredActorMembership(
                        currentUserId,
                        organizationId
                );

        OrganizationMember target =
                getRequiredMemberForUpdate(
                        memberId,
                        organizationId
                );

        List<OrganizationMember> activeOwners =
                requiresActiveOwnerProtection(target)
                        ? lockActiveOwners(organizationId)
                        : List.of();

        organizationMemberLifecyclePolicy
                .validateRemoval(
                        actor,
                        target,
                        activeOwners
                );

        OrganizationRole previousRole =
                target.getRole();

        OrganizationMemberStatus previousStatus =
                target.getStatus();

        target.remove();

        eventPublisher.publishEvent(
                new OrganizationMemberRemovedEvent(
                        organizationId,
                        currentUserId,
                        target.getId(),
                        target.getUser().getId(),
                        previousRole,
                        previousStatus
                )
        );

        organizationMemberRepository.flush();

        organizationMemberRepository.flush();
    }

    private static void requireIdentifiers(
            UUID currentUserId,
            UUID organizationId
    ) {
        Objects.requireNonNull(
                currentUserId,
                "currentUserId cannot be null."
        );

        Objects.requireNonNull(
                organizationId,
                "organizationId cannot be null."
        );
    }

    private static void requireIdentifiers(
            UUID currentUserId,
            UUID organizationId,
            UUID memberId
    ) {
        Objects.requireNonNull(
                currentUserId,
                "currentUserId cannot be null."
        );

        Objects.requireNonNull(
                organizationId,
                "organizationId cannot be null."
        );

        Objects.requireNonNull(
                memberId,
                "memberId cannot be null."
        );
    }

    private OrganizationMember getRequiredActorMembership(
            UUID currentUserId,
            UUID organizationId
    ) {
        return organizationMemberRepository
                .findByUserIdAndOrganizationId(
                        currentUserId,
                        organizationId
                )
                .orElseThrow(
                        () -> new IllegalStateException(
                                """
                                Organization access was granted but the actor \
                                membership could not be loaded.
                                userId=%s, organizationId=%s
                                """.formatted(
                                        currentUserId,
                                        organizationId
                                )
                        )
                );
    }

    private OrganizationMember getRequiredMemberForUpdate(
            UUID memberId,
            UUID organizationId
    ) {
        return organizationMemberRepository
                .findByIdAndOrganizationIdForUpdate(
                        memberId,
                        organizationId
                )
                .orElseThrow(
                        () ->
                                new OrganizationMemberNotFoundException(
                                        memberId,
                                        organizationId
                                )
                );
    }

    private boolean requiresOwnerLock(
            OrganizationMember target,
            OrganizationRole requestedRole
    ) {
        return target.getRole() ==
                OrganizationRole.ORGANIZATION_OWNER
                &&
                requestedRole !=
                        OrganizationRole.ORGANIZATION_OWNER;
    }

    private List<OrganizationMember> lockActiveOwners(
            UUID organizationId
    ) {
        return organizationMemberRepository
                .findAllByOrganizationIdAndRoleAndStatusForUpdate(
                        organizationId,
                        OrganizationRole.ORGANIZATION_OWNER,
                        OrganizationMemberStatus.ACTIVE
                );
    }

    private boolean requiresActiveOwnerProtection(
            OrganizationMember target
    ) {
        return target.getRole() ==
                OrganizationRole.ORGANIZATION_OWNER
                &&
                target.getStatus() ==
                        OrganizationMemberStatus.ACTIVE;
    }
}