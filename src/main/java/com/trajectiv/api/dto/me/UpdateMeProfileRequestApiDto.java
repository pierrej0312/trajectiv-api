package com.trajectiv.api.dto.me;

import com.trajectiv.dl.enums.CareerGoal;
import com.trajectiv.dl.enums.ExperienceLevel;
import jakarta.validation.constraints.Size;

public record UpdateMeProfileRequestApiDto(
        CareerGoal careerGoal,

        @Size(max = 180)
        String targetRole,

        ExperienceLevel experienceLevel,

        @Size(min = 2, max = 10)
        String preferredLanguage
) {
}
