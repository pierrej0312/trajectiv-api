package com.trajectiv.api.controllers.v1.me;

import com.trajectiv.api.dto.me.avatar.MeAvatarApiDto;
import com.trajectiv.api.mappers.MeApiMapper;
import com.trajectiv.api.routes.ApiRoutes;
import com.trajectiv.bll.dto.storage.StoredAvatarBllDto;
import com.trajectiv.bll.services.storage.AvatarStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(ApiRoutes.V1.ME_AVATAR)
@RequiredArgsConstructor
@SecurityRequirement(
        name = "keycloakOAuth2",
        scopes = {"openid", "profile", "email"}
)
public class MeAvatarController {

    private final AvatarStorageService avatarStorageService;
    private final MeApiMapper meApiMapper;

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
    public MeAvatarApiDto uploadAvatar(
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

        return meApiMapper.toAvatarApiDto(avatar);
    }

    @DeleteMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Delete current user avatar",
            security = @SecurityRequirement(
                    name = "keycloakOAuth2",
                    scopes = {"openid", "profile", "email"}
            )
    )
    public MeAvatarApiDto deleteAvatar(Authentication authentication) {
        StoredAvatarBllDto avatar = avatarStorageService.deleteCurrentUserAvatar(authentication);

        return meApiMapper.toAvatarApiDto(avatar);
    }
}
