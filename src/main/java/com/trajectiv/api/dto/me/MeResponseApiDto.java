package com.trajectiv.api.dto.me;

import com.trajectiv.api.dto.me.credit.MeCreditsApiDto;
import com.trajectiv.api.dto.me.onboarding.MeOnboardingApiDto;
import com.trajectiv.api.dto.me.profile.MeProfileApiDto;
import com.trajectiv.api.dto.me.subscription.MeSubscriptionApiDto;
import com.trajectiv.api.dto.me.workspace.MeWorkspaceApiDto;

import java.util.List;
import java.util.UUID;

public record MeResponseApiDto(
        UUID id,
        String keycloakSubject,
        String email,
        boolean emailVerified,
        String firstName,
        String lastName,
        String displayName,
        String avatarUrl,
        UserStatusApiDto status,
        MeOnboardingApiDto onboarding,
        MeProfileApiDto profile,
        MeSubscriptionApiDto subscription,
        MeCreditsApiDto credits,
        List<MeWorkspaceApiDto> workspaces
) {

    public MeResponseApiDto {
        workspaces = workspaces == null
                ? List.of()
                : List.copyOf(workspaces);
    }
}