package com.trajectiv.api.controllers.v1.organization;

import com.trajectiv.api.dto.organization.CreateOrganizationRequestApiDto;
import com.trajectiv.api.dto.organization.OrganizationResponseApiDto;
import com.trajectiv.api.dto.organization.UpdateOrganizationRequestApiDto;
import com.trajectiv.api.mappers.OrganizationApiMapper;
import com.trajectiv.api.routes.ApiRoutes;
import com.trajectiv.bll.dto.organization.OrganizationBllDto;
import com.trajectiv.bll.services.me.sync.UserSyncService;
import com.trajectiv.bll.services.organization.OrganizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(
        value = ApiRoutes.V1.ORGANIZATIONS,
        produces = MediaType.APPLICATION_JSON_VALUE
)
@RequiredArgsConstructor
@SecurityRequirement(
        name = "keycloakOAuth2",
        scopes = {"openid", "profile", "email"}
)
public class OrganizationController {

    private final OrganizationService organizationService;
    private final UserSyncService userSyncService;
    private final OrganizationApiMapper organizationApiMapper;

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Create an organization")
    public ResponseEntity<OrganizationResponseApiDto>
    createOrganization(
            Authentication authentication,
            @Valid
            @RequestBody
            CreateOrganizationRequestApiDto request
    ) {
        UUID currentUserId =
                userSyncService.syncFromAuthentication(
                authentication
        ).getId();

        OrganizationBllDto organization =
                organizationService.createForCurrentUser(
                        currentUserId,
                        organizationApiMapper.toBllCommand(
                                request
                        )
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        organizationApiMapper.toApiDto(
                                organization
                        )
                );
    }

    @GetMapping("/{organizationId}")
    @Operation(summary = "Get an accessible organization")
    public ResponseEntity<OrganizationResponseApiDto>
    getOrganization(
            Authentication authentication,
            @PathVariable
            UUID organizationId
    ) {
        UUID currentUserId =
                userSyncService.syncFromAuthentication(
                        authentication
                ).getId();

        OrganizationBllDto organization =
                organizationService
                        .getAccessibleOrganization(
                                currentUserId,
                                organizationId
                        );

        return ResponseEntity.ok(
                organizationApiMapper.toApiDto(
                        organization
                )
        );
    }

    @PatchMapping(
            value = "/{organizationId}",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Update an organization")
    public ResponseEntity<OrganizationResponseApiDto>
    updateOrganization(
            Authentication authentication,

            @PathVariable
            UUID organizationId,

            @Valid
            @RequestBody
            UpdateOrganizationRequestApiDto request
    ) {
        UUID currentUserId =
                userSyncService.syncFromAuthentication(
                        authentication
                ).getId();


        OrganizationBllDto organization =
                organizationService.updateOrganization(
                        currentUserId,
                        organizationId,
                        organizationApiMapper.toBllCommand(
                                request
                        )
                );

        return ResponseEntity.ok(
                organizationApiMapper.toApiDto(
                        organization
                )
        );
    }

    @PostMapping("/{organizationId}/archive")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Archive an organization")
    public void archiveOrganization(
            Authentication authentication,

            @PathVariable
            UUID organizationId
    ) {
        UUID currentUserId =
                userSyncService.syncFromAuthentication(
                        authentication
                ).getId();

        organizationService.archiveOrganization(
                currentUserId,
                organizationId
        );
    }
}