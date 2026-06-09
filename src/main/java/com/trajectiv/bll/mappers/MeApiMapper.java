package com.trajectiv.bll.mappers;

import com.trajectiv.api.dto.me.*;
import com.trajectiv.bll.dto.me.MeBllDto;
import org.springframework.stereotype.Component;

@Component
public class MeApiMapper {

    public MeResponseApiDto toApiDto(MeBllDto me) {
        return new MeResponseApiDto(
                me.user().id(),
                me.user().keycloakSubject(),
                me.user().email(),
                me.user().emailVerified(),
                me.user().firstName(),
                me.user().lastName(),
                me.user().displayName(),
                me.profile().avatarUrl(),
                me.user().status(),
                toOnboardingApiDto(me),
                toProfileApiDto(me),
                toSubscriptionApiDto(me),
                toCreditsApiDto(me)
        );
    }

    private MeProfileApiDto toProfileApiDto(MeBllDto me) {
        return new MeProfileApiDto(
                me.profile().careerGoal(),
                me.profile().targetRole(),
                me.profile().experienceLevel(),
                me.profile().preferredLanguage()
        );
    }

    private MeOnboardingApiDto toOnboardingApiDto(MeBllDto me) {
        return new MeOnboardingApiDto(
                me.onboarding().status(),
                me.onboarding().completedAt(),
                me.onboarding().missingFields()
        );
    }

    private MeSubscriptionApiDto toSubscriptionApiDto(MeBllDto me) {
        return new MeSubscriptionApiDto(
                me.subscription().plan(),
                me.subscription().status()
        );
    }

    private MeCreditsApiDto toCreditsApiDto(MeBllDto me) {
        return new MeCreditsApiDto(
                me.credits().monthlyLimit(),
                me.credits().used(),
                me.credits().remaining()
        );
    }
}
