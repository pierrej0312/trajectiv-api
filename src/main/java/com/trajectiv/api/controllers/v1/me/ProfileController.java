package com.trajectiv.api.controllers.v1.me;

import com.trajectiv.api.dto.me.profile.ProfileCompletionResponseApiDto;
import com.trajectiv.api.dto.me.profile.UpdateMeProfileRequestApiDto;
import com.trajectiv.api.dto.me.profile.UpdatedMeProfileResponseApiDto;
import com.trajectiv.api.mappers.MeOnboardingApiMapper;
import com.trajectiv.api.mappers.MeProfileApiMapper;
import com.trajectiv.api.mappers.ProfileCompletionApiMapper;
import com.trajectiv.api.routes.ApiRoutes;
import com.trajectiv.bll.dto.me.profile.ProfileCompletionResponseBllDto;
import com.trajectiv.bll.dto.me.profile.UpdatedUserProfileBllDto;
import com.trajectiv.bll.services.profile.completion.ProfileCompletionService;
import com.trajectiv.bll.services.profile.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(
        value = ApiRoutes.V1.ME_PROFILE,
        produces = MediaType.APPLICATION_JSON_VALUE
)
@RequiredArgsConstructor
@SecurityRequirement(
        name = "keycloakOAuth2",
        scopes = {"openid", "profile", "email"}
)
public class ProfileController {

    private final UserProfileService userProfileService;
    private final ProfileCompletionService profileCompletionService;

    private final MeProfileApiMapper profileApiMapper;
    private final MeOnboardingApiMapper onboardingApiMapper;
    private final ProfileCompletionApiMapper profileCompletionApiMapper;

    @PatchMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Update current user profile")
    public ResponseEntity<UpdatedMeProfileResponseApiDto> updateProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateMeProfileRequestApiDto request
    ) {
        UpdatedUserProfileBllDto updatedProfile =
                userProfileService.updateCurrentUserProfile(
                        authentication,
                        profileApiMapper.toBllCommand(request)
                );

        return ResponseEntity.ok(
                new UpdatedMeProfileResponseApiDto(
                        profileApiMapper.toApiDto(
                                updatedProfile.profile()
                        ),
                        onboardingApiMapper.toApiDto(
                                updatedProfile.onboarding()
                        )
                )
        );
    }

    @GetMapping("/completion")
    @Operation(summary = "Get current profile completion")
    public ResponseEntity<ProfileCompletionResponseApiDto>
    getMyProfileCompletion(
            Authentication authentication
    ) {
        ProfileCompletionResponseBllDto profileCompletion =
                profileCompletionService.getProfileCompletion(
                        authentication
                );

        return ResponseEntity.ok(
                profileCompletionApiMapper.toApiDto(
                        profileCompletion
                )
        );
    }
}