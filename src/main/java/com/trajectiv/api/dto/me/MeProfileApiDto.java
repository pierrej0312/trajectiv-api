package com.trajectiv.api.dto.me;

import com.trajectiv.dl.enums.CareerGoal;
import com.trajectiv.dl.enums.ExperienceLevel;

public record MeProfileApiDto(
        CareerGoal careerGoal,
        String targetRole,
        ExperienceLevel experienceLevel,
        String preferredLanguage
) {
}
