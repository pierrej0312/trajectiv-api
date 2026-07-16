package com.trajectiv.api.mappers;

import com.trajectiv.api.dto.me.MeResponseApiDto;
import com.trajectiv.api.dto.me.UserStatusApiDto;
import com.trajectiv.bll.dto.me.MeBllDto;
import com.trajectiv.dl.enums.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MeApiMapper {

    private final MeProfileApiMapper profileMapper;
    private final MeOnboardingApiMapper onboardingMapper;
    private final MeSubscriptionApiMapper subscriptionMapper;
    private final MeCreditsApiMapper creditsMapper;
    private final MeWorkspaceApiMapper workspaceMapper;

    public MeResponseApiDto toApiDto(
            MeBllDto me
    ) {
        return new MeResponseApiDto(
                me.user().id(),
                me.user().keycloakSubject(),
                me.user().email(),
                me.user().emailVerified(),
                me.user().firstName(),
                me.user().lastName(),
                me.user().displayName(),
                me.profile().avatarUrl(),
                mapUserStatus(me.user().status()),
                onboardingMapper.toApiDto(
                        me.onboarding()
                ),
                profileMapper.toApiDto(
                        me.profile()
                ),
                subscriptionMapper.toApiDto(
                        me.subscription()
                ),
                creditsMapper.toApiDto(
                        me.credits()
                ),
                workspaceMapper.toApiDtos(
                        me.workspaces()
                )
        );
    }

    private UserStatusApiDto mapUserStatus(
            UserStatus status
    ) {
        return switch (status) {
            case ACTIVE ->
                    UserStatusApiDto.ACTIVE;

            case DISABLED ->
                    UserStatusApiDto.DISABLED;

            case DELETED ->
                    UserStatusApiDto.DELETED;
        };
    }
}