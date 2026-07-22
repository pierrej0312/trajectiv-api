package com.trajectiv.bll.services.organization.member;

import com.trajectiv.bll.dto.access.OrganizationPermission;
import com.trajectiv.bll.dto.organization.member.OrganizationMemberBllDto;
import com.trajectiv.bll.dto.organization.member.UpdateOrganizationMemberRoleBllCommand;
import com.trajectiv.bll.exceptions.OrganizationAccessDeniedException;
import com.trajectiv.bll.mappers.organization.OrganizationMemberBllMapper;
import com.trajectiv.bll.services.access.OrganizationAccessService;
import com.trajectiv.bll.services.organization.member.policy.OrganizationMemberLifecyclePolicy;
import com.trajectiv.bll.services.organization.member.policy.OrganizationMemberRolePolicy;
import com.trajectiv.dl.entities.User;
import com.trajectiv.dl.entities.organization.OrganizationMember;
import com.trajectiv.dl.enums.organization.OrganizationMemberStatus;
import com.trajectiv.dl.enums.organization.OrganizationRole;
import com.trajectiv.dl.repositories.organization.OrganizationMemberRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrganizationMemberServiceImplTest {

    @Mock
    private OrganizationMemberRolePolicy
            organizationMemberRolePolicy;

    @Mock
    private OrganizationMemberLifecyclePolicy
            organizationMemberLifecyclePolicy;

    @Mock
    private OrganizationAccessService
            organizationAccessService;

    @Mock
    private OrganizationMemberRepository
            organizationMemberRepository;

    @Mock
    private OrganizationMemberBllMapper
            organizationMemberMapper;

    @Mock
    private EntityManager entityManager;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    private OrganizationMemberServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new OrganizationMemberServiceImpl(
                organizationMemberRolePolicy,
                organizationMemberLifecyclePolicy,
                organizationAccessService,
                organizationMemberRepository,
                organizationMemberMapper,
                entityManager,
                applicationEventPublisher
        );
    }

    @Test
    void getMembers_requiresMemberReadPermission() {
        UUID currentUserId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();

        when(
                organizationMemberRepository
                        .findAllByOrganization_IdAndStatusInOrderByJoinedAtAsc(
                                eq(organizationId),
                                anyCollection()
                        )
        ).thenReturn(List.of());

        when(
                organizationMemberMapper.toDtos(
                        List.of()
                )
        ).thenReturn(List.of());

        List<OrganizationMemberBllDto> result =
                service.getMembers(
                        currentUserId,
                        organizationId
                );

        assertThat(result).isEmpty();

        verify(organizationAccessService)
                .requirePermission(
                        currentUserId,
                        organizationId,
                        OrganizationPermission.MEMBER_READ
                );

        verify(organizationMemberRepository)
                .findAllByOrganization_IdAndStatusInOrderByJoinedAtAsc(
                        organizationId,
                        Set.of(
                                OrganizationMemberStatus.ACTIVE,
                                OrganizationMemberStatus.SUSPENDED
                        )
                );
    }

    @Test
    void getMembers_doesNotReadMembersWhenPermissionIsDenied() {
        UUID currentUserId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();

        doThrow(
                new OrganizationAccessDeniedException(
                        currentUserId,
                        organizationId
                )
        )
                .when(organizationAccessService)
                .requirePermission(
                        currentUserId,
                        organizationId,
                        OrganizationPermission.MEMBER_READ
                );

        assertThatThrownBy(
                () -> service.getMembers(
                        currentUserId,
                        organizationId
                )
        )
                .isInstanceOf(
                        OrganizationAccessDeniedException.class
                );

        verifyNoInteractions(
                organizationMemberRepository,
                organizationMemberMapper,
                organizationMemberRolePolicy
        );
    }

    @Test
    void changeRole_updatesActiveMemberWhenActorHasPermission() {
        UUID currentUserId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        UUID memberId = UUID.randomUUID();
        UUID memberUserId = UUID.randomUUID();

        OrganizationMember actor =
                mock(OrganizationMember.class);

        OrganizationMember target =
                mock(OrganizationMember.class);

        User targetUser =
                mock(User.class);

        UpdateOrganizationMemberRoleBllCommand command =
                new UpdateOrganizationMemberRoleBllCommand(
                        OrganizationRole.ORGANIZATION_ADMIN
                );

        OrganizationMemberBllDto expected =
                mock(OrganizationMemberBllDto.class);

        when(
                organizationMemberRepository
                        .findByUserIdAndOrganizationId(
                                currentUserId,
                                organizationId
                        )
        ).thenReturn(
                Optional.of(actor)
        );

        when(
                organizationMemberRepository
                        .findByIdAndOrganizationIdForUpdate(
                                memberId,
                                organizationId
                        )
        ).thenReturn(
                Optional.of(target)
        );

        when(target.getRole())
                .thenReturn(
                        OrganizationRole.TRAINER
                );

        when(target.getUser())
                .thenReturn(
                        targetUser
                );

        when(targetUser.getId())
                .thenReturn(
                        memberUserId
                );

        when(
                organizationMemberMapper.toDto(
                        target
                )
        ).thenReturn(expected);

        OrganizationMemberBllDto result =
                service.changeRole(
                        currentUserId,
                        organizationId,
                        memberId,
                        command
                );

        assertThat(result)
                .isSameAs(expected);

        verify(organizationAccessService)
                .requirePermission(
                        currentUserId,
                        organizationId,
                        OrganizationPermission.MEMBER_UPDATE_ROLE
                );

        verify(organizationMemberRolePolicy)
                .validateChange(
                        actor,
                        target,
                        OrganizationRole.ORGANIZATION_ADMIN,
                        List.of()
                );

        verify(target)
                .changeRole(
                        OrganizationRole.ORGANIZATION_ADMIN
                );
    }
}
