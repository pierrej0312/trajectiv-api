package com.trajectiv.api.controllers.v1.me;

import com.trajectiv.api.dto.me.avatar.AvatarCustomizationResponseApiDto;
import com.trajectiv.api.dto.me.avatar.CreateAvatarCustomizationRequestApiDto;
import com.trajectiv.api.dto.me.avatar.MeAvatarApiDto;
import com.trajectiv.api.dto.me.avatar.PatchAvatarCustomizationRequestApiDto;
import com.trajectiv.api.mappers.AvatarCustomizationApiMapper;
import com.trajectiv.api.mappers.MeApiMapper;
import com.trajectiv.api.mappers.MeAvatarApiMapper;
import com.trajectiv.api.routes.ApiRoutes;
import com.trajectiv.bll.dto.storage.StoredAvatarBllDto;
import com.trajectiv.bll.services.me.avatar.AvatarCustomizationService;
import com.trajectiv.bll.services.storage.AvatarStorageService;
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
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(
        value = ApiRoutes.V1.ME_AVATAR,
        produces = MediaType.APPLICATION_JSON_VALUE
)
@RequiredArgsConstructor
@SecurityRequirement(
        name = "keycloakOAuth2",
        scopes = {"openid", "profile", "email"}
)
public class MeAvatarController {

    private final AvatarStorageService avatarStorageService;
    private final MeAvatarApiMapper meAvatarApiMapper;
    private final AvatarCustomizationService avatarCustomizationService;
    private final AvatarCustomizationApiMapper avatarCustomizationApiMapper;

    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
            summary = "Upload current user avatar",
            security = @SecurityRequirement(
                    name = "keycloakOAuth2",
                    scopes = {"openid", "profile", "email"}
            )
    )
    public ResponseEntity<MeAvatarApiDto> uploadAvatar(
            Authentication authentication,

            @Parameter(
                    description = "Avatar image file. Accepted types: jpeg, png, webp.",
                    required = true
            )
            @RequestPart("file") MultipartFile file
    ) {
        StoredAvatarBllDto avatar = avatarStorageService.uploadCurrentUserAvatar(
                authentication,
                file
        );

        return ResponseEntity.ok(
                meAvatarApiMapper.toApiDto(avatar)
        );
    }

    @DeleteMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Delete current user avatar",
            security = @SecurityRequirement(
                    name = "keycloakOAuth2",
                    scopes = {"openid", "profile", "email"}
            )
    )
    public ResponseEntity<MeAvatarApiDto> deleteAvatar(Authentication authentication) {
        StoredAvatarBllDto avatar = avatarStorageService.deleteCurrentUserAvatar(authentication);

        return ResponseEntity.ok(
                meAvatarApiMapper.toApiDto(avatar)
        );
    }

    @PostMapping(
            value = "/customization",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create current user avatar customization")
    public ResponseEntity<AvatarCustomizationResponseApiDto> createAvatarCustomization(
            Authentication authentication,
            @Valid @RequestBody CreateAvatarCustomizationRequestApiDto request
    ) {
        var createdCustomization = avatarCustomizationService.createCurrentUserAvatarCustomization(
                authentication,
                avatarCustomizationApiMapper.toBllCommand(request)
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(avatarCustomizationApiMapper.toApiDto(createdCustomization));
    }

    @GetMapping(
            value = "/customization",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Get current user avatar customization")
    public ResponseEntity<AvatarCustomizationResponseApiDto> getAvatarCustomization(Authentication authentication) {
        var customization = avatarCustomizationService.getCurrentUserAvatarCustomization(authentication);

        return ResponseEntity.ok(avatarCustomizationApiMapper.toApiDto(customization));
    }

    @PatchMapping(
            value = "/customization",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Patch current user avatar customization")
    public ResponseEntity<AvatarCustomizationResponseApiDto> patchAvatarCustomization(
            Authentication authentication,
            @Valid @RequestBody PatchAvatarCustomizationRequestApiDto request
    ) {
        var updatedCustomization = avatarCustomizationService.patchCurrentUserAvatarCustomization(
                authentication,
                avatarCustomizationApiMapper.toBllCommand(request)
        );

        return ResponseEntity.ok(avatarCustomizationApiMapper.toApiDto(updatedCustomization));
    }

    @DeleteMapping("/customization")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete current user avatar customization")
    public void deleteAvatarCustomization(Authentication authentication) {
        avatarCustomizationService.deleteCurrentUserAvatarCustomization(authentication);
    }
}
