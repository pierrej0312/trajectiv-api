package com.trajectiv.api.controllers.v1.me;

import ch.qos.logback.core.boolex.EvaluationException;
import com.trajectiv.api.dto.me.UpdateMeProfileRequestApiDto;
import com.trajectiv.api.dto.me.UpdatedMeProfileResponseApiDto;
import com.trajectiv.api.mappers.MeApiMapper;
import com.trajectiv.api.routes.ApiRoutes;
import com.trajectiv.bll.dto.me.UpdatedUserProfileBllDto;
import com.trajectiv.bll.services.profile.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(
        value = ApiRoutes.V1.ME_PROFILE,
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@SecurityRequirement(
        name = "keycloakOAuth2",
        scopes = {"openid", "profile", "email"}
)
public class ProfileController {

    private final UserProfileService userProfileService;
    private final MeApiMapper meApiMapper;

    @PatchMapping
    @Operation(summary = "Update current user profile")
    public UpdatedMeProfileResponseApiDto updateProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateMeProfileRequestApiDto request
    ) {
        UpdatedUserProfileBllDto updatedProfile = userProfileService.updateCurrentUserProfile(
                authentication,
                meApiMapper.toBllCommand(request)
        );

        return meApiMapper.toUpdatedProfileApiDto(updatedProfile);
    }
}
