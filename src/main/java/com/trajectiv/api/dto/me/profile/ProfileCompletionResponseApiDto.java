package com.trajectiv.api.dto.me.profile;

import com.trajectiv.bll.dto.me.profile.ProfileCompletionRequirement;
import com.trajectiv.bll.dto.me.profile.RecommendedProfileAction;

import java.util.List;

public record ProfileCompletionResponseApiDto(
        int completionPercentage,
        List<ProfileCompletionRequirement> missingFields,
        List<RecommendedProfileAction> recommendedActions
) {

    public ProfileCompletionResponseApiDto {
        missingFields = missingFields == null
                ? List.of()
                : List.copyOf(missingFields);

        recommendedActions = recommendedActions == null
                ? List.of()
                : List.copyOf(recommendedActions);
    }
}