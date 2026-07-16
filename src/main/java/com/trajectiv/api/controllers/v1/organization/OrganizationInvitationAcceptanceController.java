package com.trajectiv.api.controllers.v1.organization;

import com.trajectiv.api.dto.organization.invitation.AcceptOrganizationInvitationRequestApiDto;
import com.trajectiv.api.dto.organization.invitation.OrganizationInvitationAcceptanceResponseApiDto;
import com.trajectiv.api.mappers.OrganizationInvitationApiMapper;
import com.trajectiv.api.routes.ApiRoutes;
import com.trajectiv.bll.dto.organization.invitation.OrganizationInvitationAcceptanceBllDto;
import com.trajectiv.bll.services.me.sync.UserSyncService;
import com.trajectiv.bll.services.organization.invitation.OrganizationInvitationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(
        value = ApiRoutes.V1.ORGANIZATION_INVITATION_ACCEPTANCE,
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
public class OrganizationInvitationAcceptanceController {

    private final OrganizationInvitationService
            invitationService;

    private final UserSyncService userSyncService;

    private final OrganizationInvitationApiMapper
            invitationApiMapper;

    @PostMapping(
            value = "/accept",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
            summary = "Accept an organization invitation",
            description = """
                    Accepts an invitation for the authenticated user.
                    The user's verified email must match the invitation email.
                    A membership is created or restored atomically.
                    """
    )
    public ResponseEntity<OrganizationInvitationAcceptanceResponseApiDto>
    acceptInvitation(
            Authentication authentication,

            @Valid
            @RequestBody
            AcceptOrganizationInvitationRequestApiDto request
    ) {
        UUID currentUserId =
                userSyncService.syncFromAuthentication(
                        authentication
                ).getId();

        OrganizationInvitationAcceptanceBllDto acceptance =
                invitationService.accept(
                        currentUserId,
                        invitationApiMapper.toBllCommand(
                                request
                        )
                );

        return ResponseEntity.ok(
                invitationApiMapper.toApiDto(
                        acceptance
                )
        );
    }
}