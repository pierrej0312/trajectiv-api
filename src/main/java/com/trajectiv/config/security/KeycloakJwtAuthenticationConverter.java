package com.trajectiv.config.security;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component
public class KeycloakJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private static final String REALM_ACCESS = "realm_access";
    private static final String RESOURCE_ACCESS = "resource_access";
    private static final String ROLES = "roles";

    @Override
    public AbstractAuthenticationToken convert(@NotEmpty Jwt jwt) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();

        authorities.addAll(extractRealmRoles(jwt));
        authorities.addAll(extractClientRoles(jwt, "trajectiv-web"));

        return new JwtAuthenticationToken(jwt, authorities, getPrincipalName(jwt));
    }

    private String getPrincipalName(Jwt jwt) {
        String preferredUsername = jwt.getClaimAsString("preferred_username");
        if (preferredUsername != null && !preferredUsername.isBlank()) {
            return preferredUsername;
        }

        String email = jwt.getClaimAsString("email");
        if (email != null && !email.isBlank()) {
            return email;
        }

        return jwt.getSubject();
    }

    @SuppressWarnings("unchecked")
    private Collection<GrantedAuthority> extractRealmRoles(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaimAsMap(REALM_ACCESS);
        if (realmAccess == null || !realmAccess.containsKey(ROLES)) {
            return List.of();
        }

        Object rolesObject = realmAccess.get(ROLES);
        if (!(rolesObject instanceof Collection<?> roles)) {
            return List.of();
        }

        return roles.stream()
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .map(GrantedAuthority.class::cast)
                .toList();
    }

    @SuppressWarnings("unchecked")
    private Collection<GrantedAuthority> extractClientRoles(Jwt jwt, String clientId) {
        Map<String, Object> resourceAccess = jwt.getClaimAsMap(RESOURCE_ACCESS);
        if (resourceAccess == null || !resourceAccess.containsKey(clientId)) {
            return List.of();
        }

        Object clientAccessObject = resourceAccess.get(clientId);
        if (!(clientAccessObject instanceof Map<?, ?> clientAccess)) {
            return List.of();
        }

        Object rolesObject = clientAccess.get(ROLES);
        if (!(rolesObject instanceof Collection<?> roles)) {
            return List.of();
        }

        return roles.stream()
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .map(GrantedAuthority.class::cast)
                .toList();
    }
}