package com.trajectiv.api.dto.me;

import com.trajectiv.bll.dto.me.ProfileCompletionRequirement;
import com.trajectiv.bll.dto.me.RecommendedProfileAction;

import java.util.List;

public record ProfileCompletionResponseApiDto(
        int completionPercentage,
        List<ProfileCompletionRequirement> missingFields,
        List<RecommendedProfileAction> recommendedActions
) {
}
