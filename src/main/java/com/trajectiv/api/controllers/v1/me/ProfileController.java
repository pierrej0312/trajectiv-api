package com.trajectiv.api.controllers.v1.me;

import com.trajectiv.api.dto.me.ProfileCompletionResponseApiDto;
import com.trajectiv.api.dto.me.UpdateMeProfileRequestApiDto;
import com.trajectiv.api.dto.me.UpdatedMeProfileResponseApiDto;
import com.trajectiv.api.mappers.MeApiMapper;
import com.trajectiv.api.routes.ApiRoutes;
import com.trajectiv.bll.dto.me.ProfileCompletionResponseBllDto;
import com.trajectiv.bll.dto.me.UpdatedUserProfileBllDto;
import com.trajectiv.bll.services.profile.ProfileCompletionService;
import com.trajectiv.bll.services.profile.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(
        value = ApiRoutes.V1.ME_PROFILE,
        produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@SecurityRequirement(
        name = "keycloakOAuth2",
        scopes = {"openid", "profile", "email"}
)
public class ProfileController {

    private final UserProfileService userProfileService;
    private final ProfileCompletionService profileCompletionService;
    private final MeApiMapper meApiMapper;

    @PatchMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update current user profile")
    public ResponseEntity<UpdatedMeProfileResponseApiDto> updateProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateMeProfileRequestApiDto request
    ) {
        UpdatedUserProfileBllDto updatedProfile = userProfileService.updateCurrentUserProfile(
                authentication,
                meApiMapper.toBllCommand(request)
        );

        return ResponseEntity.ok(meApiMapper.toUpdatedProfileApiDto(updatedProfile));
    }

    @GetMapping("/completion")
    @Operation(summary = "Get current profile completion")
    public ResponseEntity<ProfileCompletionResponseApiDto> getMyProfileCompletion(Authentication authentication) {
        ProfileCompletionResponseBllDto profileCompletion = profileCompletionService.getProfileCompletion(authentication);
        return ResponseEntity.ok(meApiMapper.toProfileCompletionApiDto(profileCompletion));
    }
}
