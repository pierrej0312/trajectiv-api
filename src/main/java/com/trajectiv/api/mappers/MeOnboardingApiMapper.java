package com.trajectiv.api.mappers;

import com.trajectiv.api.dto.me.onboarding.MeOnboardingApiDto;
import com.trajectiv.api.dto.me.onboarding.OnboardingStatusApiDto;
import com.trajectiv.bll.dto.me.onboarding.MeOnboardingBllDto;
import org.springframework.stereotype.Component;

@Component
public class MeOnboardingApiMapper {

    public MeOnboardingApiDto toApiDto(
            MeOnboardingBllDto onboarding
    ) {
        return new MeOnboardingApiDto(
                mapStatus(onboarding.status()),
                onboarding.completedAt(),
                onboarding.missingFields()
        );
    }

    private OnboardingStatusApiDto mapStatus(
            com.trajectiv.dl.enums.OnboardingStatus status
    ) {
        return switch (status) {
            case NOT_STARTED ->
                    OnboardingStatusApiDto.NOT_STARTED;

            case IN_PROGRESS ->
                    OnboardingStatusApiDto.IN_PROGRESS;

            case COMPLETED ->
                    OnboardingStatusApiDto.COMPLETED;
        };
    }
}