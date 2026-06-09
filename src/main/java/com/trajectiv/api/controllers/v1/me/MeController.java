package com.trajectiv.api.controllers.v1.me;

import com.trajectiv.api.dto.me.MeResponseApiDto;
import com.trajectiv.api.routes.ApiRoutes;
import com.trajectiv.bll.dto.me.MeBllDto;
import com.trajectiv.api.mappers.MeApiMapper;
import com.trajectiv.bll.services.me.MeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiRoutes.V1.ME)
@RequiredArgsConstructor
@SecurityRequirement(
        name = "keycloakOAuth2",
        scopes = {"openid", "profile", "email"}
)
public class MeController {

    private final MeService meService;
    private final MeApiMapper meApiMapper;

    @GetMapping
    @Operation(summary = "Get current authenticated user context")
    public MeResponseApiDto getMe(Authentication authentication) {
        MeBllDto me = meService.getMe(authentication);

        return meApiMapper.toApiDto(me);
    }
}
