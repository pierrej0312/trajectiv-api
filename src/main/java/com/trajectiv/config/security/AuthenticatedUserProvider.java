package com.trajectiv.config.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class AuthenticatedUserProvider {

    public AuthenticatedUserClaims getClaims(Authentication authentication) {
        if (!(authentication instanceof JwtAuthenticationToken jwtAuthenticationToken)) {
            throw new IllegalStateException("Authentication is not a JWT authentication token.");
        }

        Jwt jwt = jwtAuthenticationToken.getToken();
        return AuthenticatedUserClaims.from(jwt);
    }
}