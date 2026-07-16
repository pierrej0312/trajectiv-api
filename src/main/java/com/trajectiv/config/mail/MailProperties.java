package com.trajectiv.config.mail;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
        prefix = "trajectiv.mail"
)
public record MailProperties(
        String fromAddress,
        String invitationBaseUrl
) {
}