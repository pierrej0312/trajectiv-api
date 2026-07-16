package com.trajectiv.bll.services.organization.invitation;

import com.trajectiv.bll.dto.access.OrganizationPermission;
import com.trajectiv.bll.dto.notification.OrganizationInvitationEmailBllDto;
import com.trajectiv.bll.dto.organization.invitation.AcceptOrganizationInvitationBllCommand;
import com.trajectiv.bll.dto.organization.invitation.CreateOrganizationInvitationBllCommand;
import com.trajectiv.bll.dto.organization.invitation.OrganizationInvitationAcceptanceBllDto;
import com.trajectiv.bll.dto.organization.invitation.OrganizationInvitationBllDto;
import com.trajectiv.bll.events.organization.OrganizationInvitationCreatedEvent;
import com.trajectiv.bll.exceptions.*;
import com.trajectiv.bll.mappers.organization.OrganizationInvitationBllMapper;
import com.trajectiv.bll.services.access.OrganizationAccessService;
import com.trajectiv.bll.services.organization.invitation.policy.OrganizationInvitationPolicy;
import com.trajectiv.dl.entities.User;
import com.trajectiv.dl.entities.organization.Organization;
import com.trajectiv.dl.entities.organization.OrganizationInvitation;
import com.trajectiv.dl.entities.organization.OrganizationMember;
import com.trajectiv.dl.enums.organization.OrganizationInvitationStatus;
import com.trajectiv.dl.enums.organization.OrganizationMemberStatus;
import com.trajectiv.dl.repositories.UserRepository;
import com.trajectiv.dl.repositories.organization.OrganizationInvitationRepository;
import com.trajectiv.dl.repositories.organization.OrganizationMemberRepository;
import com.trajectiv.dl.repositories.organization.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrganizationInvitationServiceImpl
        implements OrganizationInvitationService {

    private final UserRepository userRepository;

    private final OrganizationRepository
            organizationRepository;

    private final OrganizationInvitationRepository
            invitationRepository;

    private final OrganizationMemberRepository
            memberRepository;

    private final OrganizationAccessService
            accessService;

    private final OrganizationInvitationTokenGenerator
            tokenGenerator;

    private final OrganizationInvitationTokenHasher
            tokenHasher;

    private final OrganizationInvitationUrlFactory
            invitationUrlFactory;

    private final OrganizationInvitationPolicy
            invitationPolicy;

    private final OrganizationInvitationBllMapper
            invitationMapper;

    private final ApplicationEventPublisher
            eventPublisher;

    private final Clock clock;

    private static final Set<OrganizationMemberStatus>
            EXISTING_MEMBERSHIP_STATUSES =
            Set.of(
                    OrganizationMemberStatus.ACTIVE,
                    OrganizationMemberStatus.SUSPENDED
            );

    @Override
    @Transactional
    public OrganizationInvitationBllDto invite(
            UUID currentUserId,
            UUID organizationId,
            CreateOrganizationInvitationBllCommand command
    ) {
        requireIdentifiers(
                currentUserId,
                organizationId
        );

        Objects.requireNonNull(
                command,
                "command cannot be null."
        );

        accessService.requirePermission(
                currentUserId,
                organizationId,
                OrganizationPermission.MEMBER_INVITE
        );

        User inviter =
                getRequiredUser(currentUserId);

        Organization organization =
                getRequiredActiveOrganization(
                        organizationId
                );

        return createInvitation(
                inviter,
                organization,
                command
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganizationInvitationBllDto>
    getInvitations(
            UUID currentUserId,
            UUID organizationId
    ) {
        requireIdentifiers(
                currentUserId,
                organizationId
        );

        accessService.requirePermission(
                currentUserId,
                organizationId,
                OrganizationPermission.MEMBER_READ
        );

        return invitationRepository
                .findAllByOrganizationIdWithRelations(
                        organizationId
                )
                .stream()
                .map(invitationMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public OrganizationInvitationAcceptanceBllDto accept(
            UUID currentUserId,
            AcceptOrganizationInvitationBllCommand command
    ) {
        Objects.requireNonNull(
                currentUserId,
                "currentUserId cannot be null."
        );

        Objects.requireNonNull(
                command,
                "command cannot be null."
        );

        User user = getRequiredUser(currentUserId);

        requireVerifiedEmail(user);

        String tokenHash =
                tokenHasher.hash(command.token());

        OrganizationInvitation invitation =
                invitationRepository
                        .findByTokenHashForUpdate(
                                tokenHash
                        )
                        .orElseThrow(
                                OrganizationInvitationNotFoundException::new
                        );

        Instant now = Instant.now(clock);

        validateInvitationForAcceptance(
                invitation,
                user,
                now
        );

        OrganizationMember membership =
                createOrRestoreMembership(
                        invitation,
                        user,
                        now
                );

        invitation.accept(user, now);

        return new OrganizationInvitationAcceptanceBllDto(
                invitation.getId(),
                invitation.getOrganization().getId(),
                membership.getId(),
                membership.getRole(),
                invitation.getAcceptedAt()
        );
    }

    @Override
    @Transactional
    public void revoke(
            UUID currentUserId,
            UUID organizationId,
            UUID invitationId
    ) {
        requireIdentifiers(
                currentUserId,
                organizationId
        );

        Objects.requireNonNull(
                invitationId,
                "invitationId cannot be null."
        );

        accessService.requirePermission(
                currentUserId,
                organizationId,
                OrganizationPermission.MEMBER_INVITE
        );

        OrganizationInvitation invitation =
                getRequiredInvitation(
                        organizationId,
                        invitationId
                );

        validatePendingInvitation(invitation);

        invitation.revoke(
                Instant.now(clock)
        );
    }

    @Override
    @Transactional
    public OrganizationInvitationBllDto resend(
            UUID currentUserId,
            UUID organizationId,
            UUID invitationId
    ) {
        requireIdentifiers(
                currentUserId,
                organizationId
        );

        Objects.requireNonNull(
                invitationId,
                "invitationId cannot be null."
        );

        accessService.requirePermission(
                currentUserId,
                organizationId,
                OrganizationPermission.MEMBER_INVITE
        );

        OrganizationInvitation previousInvitation =
                getRequiredInvitation(
                        organizationId,
                        invitationId
                );

        validatePendingInvitation(
                previousInvitation
        );

        previousInvitation.revoke(
                Instant.now(clock)
        );

        User inviter =
                getRequiredUser(currentUserId);

        CreateOrganizationInvitationBllCommand command =
                new CreateOrganizationInvitationBllCommand(
                        previousInvitation.getEmail(),
                        previousInvitation.getRole()
                );

        return createInvitation(
                inviter,
                previousInvitation.getOrganization(),
                command
        );
    }

    private OrganizationInvitationBllDto createInvitation(
            User inviter,
            Organization organization,
            CreateOrganizationInvitationBllCommand command
    ) {
        ensureNoPendingInvitation(
                organization.getId(),
                command.email()
        );

        ensureNotAlreadyMember(
                organization.getId(),
                command.email()
        );

        Instant now = Instant.now(clock);

        String rawToken = tokenGenerator.generate();
        String tokenHash = tokenHasher.hash(rawToken);

        OrganizationInvitation invitation =
                OrganizationInvitation.create(
                        organization,
                        command.email(),
                        command.role(),
                        tokenHash,
                        inviter,
                        invitationPolicy.resolveExpiration(
                                now
                        )
                );

        OrganizationInvitation savedInvitation =
                invitationRepository.save(
                        invitation
                );

        publishEmailEvent(
                savedInvitation,
                inviter,
                rawToken
        );

        return invitationMapper.toDto(
                savedInvitation
        );
    }

    private void publishEmailEvent(
            OrganizationInvitation invitation,
            User inviter,
            String rawToken
    ) {
        String acceptanceUrl =
                invitationUrlFactory
                        .createAcceptanceUrl(
                                rawToken
                        );

        OrganizationInvitationEmailBllDto email =
                new OrganizationInvitationEmailBllDto(
                        invitation.getEmail(),
                        invitation.getOrganization().getName(),
                        inviter.getDisplayName(),
                        invitation.getRole().name(),
                        acceptanceUrl,
                        invitation.getExpiresAt()
                );

        eventPublisher.publishEvent(
                new OrganizationInvitationCreatedEvent(
                        email
                )
        );
    }

    private OrganizationMember createOrRestoreMembership(
            OrganizationInvitation invitation,
            User user,
            Instant now
    ) {
        return memberRepository
                .findByUserIdAndOrganizationId(
                        user.getId(),
                        invitation.getOrganization().getId()
                )
                .map(
                        existingMember ->
                                restoreRemovedMembership(
                                        existingMember,
                                        invitation,
                                        now
                                )
                )
                .orElseGet(
                        () -> memberRepository.save(
                                OrganizationMember
                                        .createActive(
                                                user,
                                                invitation.getOrganization(),
                                                invitation.getRole()
                                        )
                        )
                );
    }

    private OrganizationMember restoreRemovedMembership(
            OrganizationMember member,
            OrganizationInvitation invitation,
            Instant joinedAt
    ) {
        if (
                member.getStatus() !=
                        OrganizationMemberStatus.REMOVED
        ) {
            throw new InvalidOrganizationInvitationException(
                    BusinessErrorCode
                            .ORGANIZATION_MEMBER_ALREADY_EXISTS,
                    "User is already a member of this organization."
            );
        }

        member.restore(
                invitation.getRole(),
                joinedAt
        );

        return member;
    }

    private void validateInvitationForAcceptance(
            OrganizationInvitation invitation,
            User user,
            Instant now
    ) {
        switch (invitation.getStatus()) {
            case ACCEPTED -> throw new InvalidOrganizationInvitationException(
                    BusinessErrorCode
                            .ORGANIZATION_INVITATION_ALREADY_ACCEPTED,
                    "Invitation has already been accepted."
            );

            case REVOKED -> throw new InvalidOrganizationInvitationException(
                    BusinessErrorCode
                            .ORGANIZATION_INVITATION_REVOKED,
                    "Invitation has been revoked."
            );

            case EXPIRED -> throw new InvalidOrganizationInvitationException(
                    BusinessErrorCode
                            .ORGANIZATION_INVITATION_EXPIRED,
                    "Invitation has expired."
            );

            case PENDING -> {
                // Continue.
            }
        }

        if (!now.isBefore(invitation.getExpiresAt())) {
            invitation.markExpired();

            throw new InvalidOrganizationInvitationException(
                    BusinessErrorCode
                            .ORGANIZATION_INVITATION_EXPIRED,
                    "Invitation has expired."
            );
        }

        if (
                !invitation.getEmail()
                        .equalsIgnoreCase(
                                user.getEmail()
                        )
        ) {
            throw new InvalidOrganizationInvitationException(
                    BusinessErrorCode
                            .ORGANIZATION_INVITATION_EMAIL_MISMATCH,
                    "Authenticated user email does not match invitation email."
            );
        }

        if (!invitation.getOrganization().isActive()) {
            throw new OrganizationNotActiveException(
                    invitation.getOrganization().getId()
            );
        }
    }

    private void requireVerifiedEmail(
            User user
    ) {
        if (!user.isEmailVerified()) {
            throw new InvalidOrganizationInvitationException(
                    BusinessErrorCode
                            .ORGANIZATION_INVITATION_EMAIL_NOT_VERIFIED,
                    "Authenticated user email must be verified."
            );
        }
    }

    private void ensureNoPendingInvitation(
            UUID organizationId,
            String email
    ) {
        boolean exists =
                invitationRepository
                        .existsByOrganizationIdAndEmailIgnoreCaseAndStatus(
                                organizationId,
                                email,
                                OrganizationInvitationStatus.PENDING
                        );

        if (exists) {
            throw new InvalidOrganizationInvitationException(
                    BusinessErrorCode
                            .ORGANIZATION_INVITATION_ALREADY_PENDING,
                    "A pending invitation already exists for this email."
            );
        }
    }

    private void ensureNotAlreadyMember(
            UUID organizationId,
            String email
    ) {
        boolean alreadyMember =
                memberRepository
                        .existsByOrganizationIdAndEmailAndStatuses(
                                organizationId,
                                email,
                                EXISTING_MEMBERSHIP_STATUSES
                        );

        if (alreadyMember) {
            throw new InvalidOrganizationInvitationException(
                    BusinessErrorCode
                            .ORGANIZATION_MEMBER_ALREADY_EXISTS,
                    "User is already a member of this organization."
            );
        }
    }

    private OrganizationInvitation getRequiredInvitation(
            UUID organizationId,
            UUID invitationId
    ) {
        return invitationRepository
                .findByIdAndOrganizationIdWithRelations(
                        invitationId,
                        organizationId
                )
                .orElseThrow(
                        () ->
                                new OrganizationInvitationNotFoundException(
                                        invitationId
                                )
                );
    }

    private Organization getRequiredActiveOrganization(
            UUID organizationId
    ) {
        Organization organization =
                organizationRepository
                        .findById(organizationId)
                        .orElseThrow(
                                () ->
                                        new OrganizationNotFoundException(
                                                organizationId
                                        )
                        );

        if (!organization.isActive()) {
            throw new OrganizationNotActiveException(
                    organizationId
            );
        }

        return organization;
    }

    private User getRequiredUser(
            UUID userId
    ) {
        return userRepository
                .findById(userId)
                .orElseThrow(
                        UserNotFoundException::new
                );
    }

    private void validatePendingInvitation(
            OrganizationInvitation invitation
    ) {
        if (
                invitation.getStatus() !=
                        OrganizationInvitationStatus.PENDING
        ) {
            throw new InvalidOrganizationInvitationException(
                    BusinessErrorCode
                            .INVALID_ORGANIZATION_INVITATION,
                    "Only a pending invitation can be modified."
            );
        }
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
}