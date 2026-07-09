package com.trajectiv.api.mappers;

import com.trajectiv.api.dto.me.*;
import com.trajectiv.api.dto.me.avatar.MeAvatarApiDto;
import com.trajectiv.bll.dto.me.*;
import com.trajectiv.bll.dto.storage.StoredAvatarBllDto;
import org.springframework.stereotype.Component;

import java.util.List;

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
                me.profile().targetRoleId(),
                me.profile().targetRoleLabel(),
                me.profile().targetRoleSource(),
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
    public UpdateUserProfileCommandBllDto toBllCommand(UpdateMeProfileRequestApiDto request) {
        return new UpdateUserProfileCommandBllDto(
                request.displayName(),
                request.careerGoal(),
                request.targetRoleId(),
                request.targetRoleLabel(),
                request.targetRoleSource(),
                request.experienceLevel(),
                request.preferredLanguage()
        );
    }

    public UpdatedMeProfileResponseApiDto toUpdatedProfileApiDto(UpdatedUserProfileBllDto updatedProfile) {
        return new UpdatedMeProfileResponseApiDto(
                new MeProfileApiDto(
                        updatedProfile.profile().careerGoal(),
                        updatedProfile.profile().targetRoleId(),
                        updatedProfile.profile().targetRoleLabel(),
                        updatedProfile.profile().targetRoleSource(),
                        updatedProfile.profile().experienceLevel(),
                        updatedProfile.profile().preferredLanguage()
                ),
                new MeOnboardingApiDto(
                        updatedProfile.onboarding().status(),
                        updatedProfile.onboarding().completedAt(),
                        updatedProfile.onboarding().missingFields()
                )
        );
    }

    public MeOnboardingApiDto toOnboardingApiDto(MeOnboardingBllDto onboarding) {
        return new MeOnboardingApiDto(
                onboarding.status(),
                onboarding.completedAt(),
                onboarding.missingFields()
        );
    }

    public MeAvatarApiDto toAvatarApiDto(StoredAvatarBllDto avatar) {
        return new MeAvatarApiDto(
                avatar.fileId(),
                avatar.avatarUrl()
        );
    }

    public ProfileCompletionResponseApiDto toProfileCompletionApiDto(ProfileCompletionResponseBllDto profileCompletion) {
        //TODO MAP TO API DTO
        return new ProfileCompletionResponseApiDto(
                profileCompletion.completionPercentage(),
                profileCompletion.missingFields(),
                profileCompletion.recommendedActions()
        );
    }
}
