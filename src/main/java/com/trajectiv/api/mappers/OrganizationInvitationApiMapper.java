package com.trajectiv.api.mappers;

import com.trajectiv.api.dto.organization.OrganizationRoleApiDto;
import com.trajectiv.api.dto.organization.invitation.AcceptOrganizationInvitationRequestApiDto;
import com.trajectiv.api.dto.organization.invitation.CreateOrganizationInvitationRequestApiDto;
import com.trajectiv.api.dto.organization.invitation.OrganizationInvitationAcceptanceResponseApiDto;
import com.trajectiv.api.dto.organization.invitation.OrganizationInvitationResponseApiDto;
import com.trajectiv.api.dto.organization.invitation.OrganizationInvitationStatusApiDto;
import com.trajectiv.bll.dto.organization.invitation.AcceptOrganizationInvitationBllCommand;
import com.trajectiv.bll.dto.organization.invitation.CreateOrganizationInvitationBllCommand;
import com.trajectiv.bll.dto.organization.invitation.OrganizationInvitationAcceptanceBllDto;
import com.trajectiv.bll.dto.organization.invitation.OrganizationInvitationBllDto;
import com.trajectiv.dl.enums.organization.OrganizationInvitationStatus;
import com.trajectiv.dl.enums.organization.OrganizationRole;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class OrganizationInvitationApiMapper {

    public CreateOrganizationInvitationBllCommand toBllCommand(
            CreateOrganizationInvitationRequestApiDto request
    ) {
        Objects.requireNonNull(
                request,
                "request cannot be null."
        );

        return new CreateOrganizationInvitationBllCommand(
                request.email(),
                mapRole(request.role())
        );
    }

    public AcceptOrganizationInvitationBllCommand toBllCommand(
            AcceptOrganizationInvitationRequestApiDto request
    ) {
        Objects.requireNonNull(
                request,
                "request cannot be null."
        );

        return new AcceptOrganizationInvitationBllCommand(
                request.token()
        );
    }

    public OrganizationInvitationResponseApiDto toApiDto(
            OrganizationInvitationBllDto invitation
    ) {
        Objects.requireNonNull(
                invitation,
                "invitation cannot be null."
        );

        return new OrganizationInvitationResponseApiDto(
                invitation.id(),
                invitation.organizationId(),
                invitation.organizationName(),
                invitation.email(),
                mapRole(invitation.role()),
                mapStatus(invitation.status()),
                invitation.invitedByUserId(),
                invitation.inviterDisplayName(),
                invitation.expiresAt(),
                invitation.acceptedAt(),
                invitation.revokedAt(),
                invitation.createdAt(),
                invitation.updatedAt()
        );
    }

    public List<OrganizationInvitationResponseApiDto> toApiDtos(
            List<OrganizationInvitationBllDto> invitations
    ) {
        if (invitations == null || invitations.isEmpty()) {
            return List.of();
        }

        return invitations.stream()
                .map(this::toApiDto)
                .toList();
    }

    public OrganizationInvitationAcceptanceResponseApiDto toApiDto(
            OrganizationInvitationAcceptanceBllDto acceptance
    ) {
        Objects.requireNonNull(
                acceptance,
                "acceptance cannot be null."
        );

        return new OrganizationInvitationAcceptanceResponseApiDto(
                acceptance.invitationId(),
                acceptance.organizationId(),
                acceptance.membershipId(),
                mapRole(acceptance.role()),
                acceptance.acceptedAt()
        );
    }

    private OrganizationRole mapRole(
            OrganizationRoleApiDto role
    ) {
        return switch (role) {
            case ORGANIZATION_OWNER ->
                    OrganizationRole.ORGANIZATION_OWNER;

            case ORGANIZATION_ADMIN ->
                    OrganizationRole.ORGANIZATION_ADMIN;

            case RECRUITER ->
                    OrganizationRole.RECRUITER;

            case COACH ->
                    OrganizationRole.COACH;

            case TRAINER ->
                    OrganizationRole.TRAINER;

            case LEARNER ->
                    OrganizationRole.LEARNER;
        };
    }

    private OrganizationRoleApiDto mapRole(
            OrganizationRole role
    ) {
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

    private OrganizationInvitationStatusApiDto mapStatus(
            OrganizationInvitationStatus status
    ) {
        return switch (status) {
            case PENDING ->
                    OrganizationInvitationStatusApiDto.PENDING;

            case ACCEPTED ->
                    OrganizationInvitationStatusApiDto.ACCEPTED;

            case REVOKED ->
                    OrganizationInvitationStatusApiDto.REVOKED;

            case EXPIRED ->
                    OrganizationInvitationStatusApiDto.EXPIRED;
        };
    }
}