package com.trajectiv.bll.dto.me;

import com.trajectiv.bll.dto.billing.UserSubscriptionBllDto;
import com.trajectiv.bll.dto.credits.UserAiCreditWalletBllDto;
import com.trajectiv.bll.dto.me.onboarding.MeOnboardingBllDto;
import com.trajectiv.bll.dto.me.profile.UserProfileBllDto;
import com.trajectiv.bll.dto.me.workspace.MeWorkspaceBllDto;

import java.util.List;

public record MeBllDto(
        UserBllDto user,
        UserProfileBllDto profile,
        MeOnboardingBllDto onboarding,
        UserSubscriptionBllDto subscription,
        UserAiCreditWalletBllDto credits,
        List<MeWorkspaceBllDto> workspaces
) {

    public MeBllDto {
        workspaces = workspaces == null
                ? List.of()
                : List.copyOf(workspaces);
    }
}