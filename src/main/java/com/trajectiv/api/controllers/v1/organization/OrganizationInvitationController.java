package com.trajectiv.api.controllers.v1.organization;

import com.trajectiv.api.dto.organization.invitation.CreateOrganizationInvitationRequestApiDto;
import com.trajectiv.api.dto.organization.invitation.OrganizationInvitationResponseApiDto;
import com.trajectiv.api.mappers.OrganizationInvitationApiMapper;
import com.trajectiv.api.routes.ApiRoutes;
import com.trajectiv.bll.dto.organization.invitation.OrganizationInvitationBllDto;
import com.trajectiv.bll.services.me.sync.UserSyncService;
import com.trajectiv.bll.services.organization.invitation.OrganizationInvitationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(
        value = ApiRoutes.V1.ORGANIZATION_INVITATIONS,
        produces = MediaType.APPLICATION_JSON_VALUE
)
@RequiredArgsConstructor
@SecurityRequirement(
        name = "keycloakOAuth2",
        scopes = {
                "openid",
                "profile",
                "email"
        }
)
public class OrganizationInvitationController {

    private final OrganizationInvitationService
            invitationService;

    private final UserSyncService userSyncService;

    private final OrganizationInvitationApiMapper
            invitationApiMapper;

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
            summary = "Invite a person to an organization",
            description = """
                    Creates a pending organization invitation and sends
                    an invitation email after the database transaction commits.
                    """
    )
    public ResponseEntity<OrganizationInvitationResponseApiDto>
    createInvitation(
            Authentication authentication,

            @Parameter(
                    description = "Organization identifier.",
                    required = true
            )
            @PathVariable
            UUID organizationId,

            @Valid
            @RequestBody
            CreateOrganizationInvitationRequestApiDto request
    ) {
        UUID currentUserId =
                userSyncService.syncFromAuthentication(
                        authentication
                ).getId();

        OrganizationInvitationBllDto invitation =
                invitationService.invite(
                        currentUserId,
                        organizationId,
                        invitationApiMapper.toBllCommand(
                                request
                        )
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        invitationApiMapper.toApiDto(
                                invitation
                        )
                );
    }

    @GetMapping
    @Operation(
            summary = "List organization invitations",
            description = """
                    Returns invitations created for the organization.
                    The current user must have member read permission.
                    """
    )
    public ResponseEntity<List<OrganizationInvitationResponseApiDto>>
    getInvitations(
            Authentication authentication,

            @Parameter(
                    description = "Organization identifier.",
                    required = true
            )
            @PathVariable
            UUID organizationId
    ) {
        UUID currentUserId =
                userSyncService.syncFromAuthentication(
                        authentication
                ).getId();

        List<OrganizationInvitationBllDto> invitations =
                invitationService.getInvitations(
                        currentUserId,
                        organizationId
                );

        return ResponseEntity.ok(
                invitationApiMapper.toApiDtos(
                        invitations
                )
        );
    }

    @PostMapping("/{invitationId}/revoke")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Revoke an organization invitation",
            description = """
                    Revokes a pending invitation.
                    An accepted, expired or already revoked invitation
                    cannot be revoked again.
                    """
    )
    public void revokeInvitation(
            Authentication authentication,

            @Parameter(
                    description = "Organization identifier.",
                    required = true
            )
            @PathVariable
            UUID organizationId,

            @Parameter(
                    description = "Invitation identifier.",
                    required = true
            )
            @PathVariable
            UUID invitationId
    ) {
        UUID currentUserId =
                userSyncService.syncFromAuthentication(
                        authentication
                ).getId();

        invitationService.revoke(
                currentUserId,
                organizationId,
                invitationId
        );
    }

    @PostMapping("/{invitationId}/resend")
    @Operation(
            summary = "Resend an organization invitation",
            description = """
                    Revokes the current pending invitation, creates a new token,
                    creates a new invitation and sends a new email.
                    """
    )
    public ResponseEntity<OrganizationInvitationResponseApiDto>
    resendInvitation(
            Authentication authentication,

            @Parameter(
                    description = "Organization identifier.",
                    required = true
            )
            @PathVariable
            UUID organizationId,

            @Parameter(
                    description = "Invitation identifier.",
                    required = true
            )
            @PathVariable
            UUID invitationId
    ) {
        UUID currentUserId =
                userSyncService.syncFromAuthentication(
                        authentication
                ).getId();

        OrganizationInvitationBllDto invitation =
                invitationService.resend(
                        currentUserId,
                        organizationId,
                        invitationId
                );

        return ResponseEntity.ok(
                invitationApiMapper.toApiDto(
                        invitation
                )
        );
    }
}