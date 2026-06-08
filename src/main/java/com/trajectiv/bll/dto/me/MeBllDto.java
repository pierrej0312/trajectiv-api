package com.trajectiv.bll.dto.me;

public record MeBllDto(
        UserBllDto user,
        UserProfileBllDto profile,
        MeOnboardingBllDto onboarding,
        SubscriptionBllDto subscription,
        AiCreditWalletBllDto credits
) {
}
