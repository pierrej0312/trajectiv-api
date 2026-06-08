package com.trajectiv.config.openapi;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

        private static final String SECURITY_SCHEME_NAME = "keycloakOAuth2";

        private static final String KEYCLOAK_AUTH_URL =
                "http://localhost:8080/realms/trajectiv/protocol/openid-connect/auth";

        private static final String KEYCLOAK_TOKEN_URL =
                "http://localhost:8080/realms/trajectiv/protocol/openid-connect/token";

        @Bean
        public OpenAPI trajectivOpenApi() {
                return new OpenAPI()
                        .info(new Info()
                                .title("Trajectiv API")
                                .version("v1")
                                .description("Trajectiv backend API"))
                        .components(new Components()
                                .addSecuritySchemes(SECURITY_SCHEME_NAME, keycloakOAuth2Scheme()))
                        .addSecurityItem(new SecurityRequirement()
                                .addList(SECURITY_SCHEME_NAME, List.of("openid", "profile", "email")));
        }

        private SecurityScheme keycloakOAuth2Scheme() {
                return new SecurityScheme()
                        .type(SecurityScheme.Type.OAUTH2)
                        .description("Keycloak OAuth2 Authorization Code Flow with PKCE")
                        .flows(new OAuthFlows()
                                .authorizationCode(new OAuthFlow()
                                        .authorizationUrl(KEYCLOAK_AUTH_URL)
                                        .tokenUrl(KEYCLOAK_TOKEN_URL)
                                        .scopes(new Scopes()
                                                .addString("openid", "OpenID Connect scope")
                                                .addString("profile", "User profile information")
                                                .addString("email", "User email information")
                                        )
                                )
                        );
        }
}