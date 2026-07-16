package com.trajectiv.bll.dto.me.profile;

import java.util.List;

public record ProfileCompletionResponseBllDto(
        int completionPercentage,
        List<ProfileCompletionRequirement> missingFields,
        List<RecommendedProfileAction> recommendedActions
) {
}
