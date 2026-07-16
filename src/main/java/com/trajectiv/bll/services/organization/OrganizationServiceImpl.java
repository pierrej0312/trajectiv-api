package com.trajectiv.bll.services.organization;

import com.trajectiv.bll.dto.access.OrganizationPermission;
import com.trajectiv.bll.dto.organization.CreateOrganizationBllCommand;
import com.trajectiv.bll.dto.organization.OrganizationBllDto;
import com.trajectiv.bll.dto.organization.UpdateOrganizationBllCommand;
import com.trajectiv.bll.exceptions.OrganizationNotActiveException;
import com.trajectiv.bll.exceptions.OrganizationNotFoundException;
import com.trajectiv.bll.exceptions.OrganizationSlugAlreadyExistsException;
import com.trajectiv.bll.exceptions.UserNotFoundException;
import com.trajectiv.bll.mappers.organization.OrganizationBllMapper;
import com.trajectiv.bll.services.access.OrganizationAccessService;
import com.trajectiv.bll.services.subscription.organization.OrganizationSubscriptionService;
import com.trajectiv.dl.entities.User;
import com.trajectiv.dl.entities.organization.Organization;
import com.trajectiv.dl.entities.organization.OrganizationMember;
import com.trajectiv.dl.enums.organization.OrganizationRole;
import com.trajectiv.dl.repositories.UserRepository;
import com.trajectiv.dl.repositories.organization.OrganizationMemberRepository;
import com.trajectiv.dl.repositories.organization.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrganizationServiceImpl
        implements OrganizationService {

    private final UserRepository userRepository;

    private final OrganizationRepository
            organizationRepository;

    private final OrganizationMemberRepository
            organizationMemberRepository;

    private final OrganizationSubscriptionService
            organizationSubscriptionService;

    private final OrganizationBllMapper
            organizationMapper;

    private final OrganizationAccessService
            organizationAccessService;

    @Override
    @Transactional
    public OrganizationBllDto createForCurrentUser(
            UUID currentUserId,
            CreateOrganizationBllCommand command
    ) {
        requireUserId(currentUserId);
        Objects.requireNonNull(
                command,
                "command cannot be null."
        );

        User owner = getRequiredUser(currentUserId);

        String normalizedSlug =
                normalizeSlug(command.slug());

        ensureSlugIsAvailable(normalizedSlug);

        Organization organization =
                Organization.create(
                        normalizedSlug,
                        command.name()
                );

        Organization savedOrganization =
                organizationRepository.save(
                        organization
                );

        createOwnerMembership(
                owner,
                savedOrganization
        );

        organizationSubscriptionService
                .createStarterIfMissing(
                        savedOrganization
                );

        return organizationMapper.toDto(
                savedOrganization
        );
    }

    @Override
    @Transactional(readOnly = true)
    public OrganizationBllDto getAccessibleOrganization(
            UUID currentUserId,
            UUID organizationId
    ) {
        requireUserId(currentUserId);
        requireOrganizationId(organizationId);

        Organization organization =
                getRequiredOrganization(
                        organizationId
                );

        organizationAccessService.requirePermission(
                currentUserId,
                organizationId,
                OrganizationPermission.ORGANIZATION_READ
        );

        return organizationMapper.toDto(
                organization
        );
    }

    @Override
    @Transactional
    public OrganizationBllDto updateOrganization(
            UUID currentUserId,
            UUID organizationId,
            UpdateOrganizationBllCommand command
    ) {
        requireUserId(currentUserId);
        requireOrganizationId(organizationId);

        Objects.requireNonNull(
                command,
                "command cannot be null."
        );

        Organization organization =
                getRequiredOrganization(
                        organizationId
                );

        ensureOrganizationIsActive(
                organization
        );

        organizationAccessService.requirePermission(
                currentUserId,
                organizationId,
                OrganizationPermission.ORGANIZATION_UPDATE
        );

        applyUpdate(
                organization,
                command
        );

        /*
         * L'entité est managée par JPA dans la transaction.
         * save() n'est pas obligatoire ici.
         */
        return organizationMapper.toDto(
                organization
        );
    }

    @Override
    @Transactional
    public void archiveOrganization(
            UUID currentUserId,
            UUID organizationId
    ) {
        requireUserId(currentUserId);
        requireOrganizationId(organizationId);

        Organization organization =
                getRequiredOrganization(
                        organizationId
                );

        ensureOrganizationIsActive(
                organization
        );

        organizationAccessService.requirePermission(
                currentUserId,
                organizationId,
                OrganizationPermission.ORGANIZATION_ARCHIVE
        );

        organization.archive();
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

    private Organization getRequiredOrganization(
            UUID organizationId
    ) {
        return organizationRepository
                .findById(organizationId)
                .orElseThrow(
                        () ->
                                new OrganizationNotFoundException(
                                        organizationId
                                )
                );
    }

    private void ensureSlugIsAvailable(
            String slug
    ) {
        if (organizationRepository.existsBySlug(slug)) {
            throw new OrganizationSlugAlreadyExistsException(
                    slug
            );
        }
    }

    private void createOwnerMembership(
            User owner,
            Organization organization
    ) {
        OrganizationMember ownerMembership =
                OrganizationMember.createActive(
                        owner,
                        organization,
                        OrganizationRole
                                .ORGANIZATION_OWNER
                );

        organizationMemberRepository.save(
                ownerMembership
        );
    }

    private void applyUpdate(
            Organization organization,
            UpdateOrganizationBllCommand command
    ) {
        if (command.hasNameUpdate()) {
            organization.rename(
                    command.name()
            );
        }

        if (command.hasAvatarUpdate()) {
            if (command.avatarUrl().isBlank()) {
                organization.removeAvatar();
            } else {
                organization.updateAvatar(
                        command.avatarUrl()
                );
            }
        }
    }

    private void ensureOrganizationIsActive(
            Organization organization
    ) {
        if (!organization.isActive()) {
            throw new OrganizationNotActiveException(
                    organization.getId()
            );
        }
    }

    private static String normalizeSlug(
            String slug
    ) {
        return slug
                .trim()
                .toLowerCase();
    }

    private static void requireUserId(
            UUID userId
    ) {
        Objects.requireNonNull(
                userId,
                "currentUserId cannot be null."
        );
    }

    private static void requireOrganizationId(
            UUID organizationId
    ) {
        Objects.requireNonNull(
                organizationId,
                "organizationId cannot be null."
        );
    }
}