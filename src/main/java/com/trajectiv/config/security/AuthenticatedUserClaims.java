package com.trajectiv.config.security;

import org.springframework.security.oauth2.jwt.Jwt;

public record AuthenticatedUserClaims(
        String keycloakSubject,
        String email,
        boolean emailVerified,
        String firstName,
        String lastName,
        String preferredUsername
) {
    public static AuthenticatedUserClaims from(Jwt jwt) {
        return new AuthenticatedUserClaims(
                jwt.getSubject(),
                jwt.getClaimAsString("email"),
                Boolean.TRUE.equals(jwt.getClaim("email_verified")),
                jwt.getClaimAsString("given_name"),
                jwt.getClaimAsString("family_name"),
                jwt.getClaimAsString("preferred_username")
        );
    }
}