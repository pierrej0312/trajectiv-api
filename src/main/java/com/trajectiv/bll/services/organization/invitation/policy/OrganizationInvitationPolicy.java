package com.trajectiv.bll.services.organization.invitation.policy;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
public class OrganizationInvitationPolicy {

    private static final Duration VALIDITY =
            Duration.ofDays(7);

    public Instant resolveExpiration(
            Instant createdAt
    ) {
        return createdAt.plus(VALIDITY);
    }
}