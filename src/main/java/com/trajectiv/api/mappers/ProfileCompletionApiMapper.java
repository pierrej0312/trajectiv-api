package com.trajectiv.api.mappers;

import com.trajectiv.api.dto.me.profile.ProfileCompletionResponseApiDto;
import com.trajectiv.bll.dto.me.profile.ProfileCompletionResponseBllDto;
import org.springframework.stereotype.Component;

@Component
public class ProfileCompletionApiMapper {

    public ProfileCompletionResponseApiDto toApiDto(
            ProfileCompletionResponseBllDto profileCompletion
    ) {
        return new ProfileCompletionResponseApiDto(
                profileCompletion.completionPercentage(),
                profileCompletion.missingFields(),
                profileCompletion.recommendedActions()
        );
    }
}