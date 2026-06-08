package com.trajectiv.api.controllers.v1.security;

import com.trajectiv.api.routes.ApiRoutes;
import com.trajectiv.config.security.AuthenticatedUserClaims;
import com.trajectiv.config.security.AuthenticatedUserProvider;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiRoutes.V1.SECURITY)
@RequiredArgsConstructor
@SecurityRequirement(
        name = "keycloakOAuth2",
        scopes = {"openid", "profile", "email"}
)
public class SecurityDebugController {

    private final AuthenticatedUserProvider authenticatedUserProvider;

    @GetMapping("/me")
    public AuthenticatedUserClaims me(Authentication authentication) {
        return authenticatedUserProvider.getClaims(authentication);
    }
}