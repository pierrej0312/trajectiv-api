package com.trajectiv.api.controllers.v1.organization;

import com.trajectiv.api.dto.organization.member.OrganizationMemberResponseApiDto;
import com.trajectiv.api.dto.organization.member.UpdateOrganizationMemberRoleRequestApiDto;
import com.trajectiv.api.mappers.OrganizationMemberApiMapper;
import com.trajectiv.api.routes.ApiRoutes;
import com.trajectiv.bll.dto.organization.member.OrganizationMemberBllDto;
import com.trajectiv.bll.services.me.sync.UserSyncService;
import com.trajectiv.bll.services.organization.member.OrganizationMemberService;
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
        value = ApiRoutes.V1.ORGANIZATION_MEMBERS,
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
public class OrganizationMemberController {

    private final UserSyncService
            userSyncService;

    private final OrganizationMemberService
            organizationMemberService;

    private final OrganizationMemberApiMapper
            organizationMemberApiMapper;

    @GetMapping
    @Operation(
            summary = "List organization members",
            description = """
                    Returns active and suspended members of an organization.
                    Removed members are excluded.
                    Requires the MEMBER_READ organization permission.
                    """
    )
    public ResponseEntity<
            List<OrganizationMemberResponseApiDto>
            > getMembers(
            Authentication authentication,

            @Parameter(
                    description = "Organization identifier.",
                    required = true
            )
            @PathVariable
            UUID organizationId
    ) {
        UUID currentUserId =
                userSyncService
                        .syncFromAuthentication(
                                authentication
                        )
                        .getId();

        List<OrganizationMemberBllDto> members =
                organizationMemberService.getMembers(
                        currentUserId,
                        organizationId
                );

        return ResponseEntity.ok(
                organizationMemberApiMapper.toApiDtos(
                        members
                )
        );
    }

    @PatchMapping(
            value = "/{memberId}/role",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
            summary = "Update an organization member role",
            description = """
                Changes the role of an active organization member.
                Requires the MEMBER_UPDATE_ROLE permission.
                Only an owner can assign the owner role.
                The last active owner cannot be demoted.
                """
    )
    public ResponseEntity<OrganizationMemberResponseApiDto>
    changeRole(
            Authentication authentication,

            @Parameter(
                    description = "Organization identifier.",
                    required = true
            )
            @PathVariable
            UUID organizationId,

            @Parameter(
                    description = "Organization membership identifier.",
                    required = true
            )
            @PathVariable
            UUID memberId,

            @Valid
            @RequestBody
            UpdateOrganizationMemberRoleRequestApiDto request
    ) {
        UUID currentUserId =
                userSyncService
                        .syncFromAuthentication(
                                authentication
                        )
                        .getId();

        OrganizationMemberBllDto updatedMember =
                organizationMemberService.changeRole(
                        currentUserId,
                        organizationId,
                        memberId,
                        organizationMemberApiMapper
                                .toBllCommand(request)
                );

        return ResponseEntity.ok(
                organizationMemberApiMapper.toApiDto(
                        updatedMember
                )
        );
    }

    @PostMapping("/{memberId}/suspend")
    @Operation(
            summary = "Suspend an organization member",
            description = """
                Suspends an active organization member.
                Requires MEMBER_UPDATE_STATUS.
                An administrator cannot suspend an owner.
                The last active owner cannot be suspended.
                """
    )
    public ResponseEntity<OrganizationMemberResponseApiDto>
    suspendMember(
            Authentication authentication,
            @PathVariable UUID organizationId,
            @PathVariable UUID memberId
    ) {
        UUID currentUserId =
                userSyncService
                        .syncFromAuthentication(authentication)
                        .getId();

        OrganizationMemberBllDto member =
                organizationMemberService.suspend(
                        currentUserId,
                        organizationId,
                        memberId
                );

        return ResponseEntity.ok(
                organizationMemberApiMapper.toApiDto(
                        member
                )
        );
    }

    @PostMapping("/{memberId}/reactivate")
    @Operation(
            summary = "Reactivate an organization member",
            description = """
                Reactivates a suspended organization member.
                Requires MEMBER_UPDATE_STATUS.
                A member cannot reactivate their own membership.
                """
    )
    public ResponseEntity<OrganizationMemberResponseApiDto>
    reactivateMember(
            Authentication authentication,
            @PathVariable UUID organizationId,
            @PathVariable UUID memberId
    ) {
        UUID currentUserId =
                userSyncService
                        .syncFromAuthentication(authentication)
                        .getId();

        OrganizationMemberBllDto member =
                organizationMemberService.reactivate(
                        currentUserId,
                        organizationId,
                        memberId
                );

        return ResponseEntity.ok(
                organizationMemberApiMapper.toApiDto(
                        member
                )
        );
    }

    @DeleteMapping("/{memberId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Remove an organization member",
            description = """
                Soft-removes an organization member.
                Requires MEMBER_REMOVE.
                Removed members lose organization access immediately
                and are excluded from member listings.
                """
    )
    public void removeMember(
            Authentication authentication,
            @PathVariable UUID organizationId,
            @PathVariable UUID memberId
    ) {
        UUID currentUserId =
                userSyncService
                        .syncFromAuthentication(authentication)
                        .getId();

        organizationMemberService.remove(
                currentUserId,
                organizationId,
                memberId
        );
    }
}