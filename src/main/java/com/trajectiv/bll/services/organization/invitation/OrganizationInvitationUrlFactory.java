package com.trajectiv.bll.services.organization.invitation;

import com.trajectiv.config.mail.MailProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class OrganizationInvitationUrlFactory {

    private final MailProperties mailProperties;

    public String createAcceptanceUrl(
            String rawToken
    ) {
        if (rawToken == null || rawToken.isBlank()) {
            throw new IllegalArgumentException(
                    "Invitation token cannot be blank."
            );
        }

        String baseUrl =
                removeTrailingSlash(
                        mailProperties.invitationBaseUrl()
                );

        String encodedToken =
                URLEncoder.encode(
                        rawToken,
                        StandardCharsets.UTF_8
                );

        return baseUrl
                + "?token="
                + encodedToken;
    }

    private String removeTrailingSlash(
            String value
    ) {
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                    "trajectiv.mail.invitation-base-url cannot be blank."
            );
        }

        String trimmed = value.trim();

        return trimmed.endsWith("/")
                ? trimmed.substring(
                0,
                trimmed.length() - 1
        )
                : trimmed;
    }
}