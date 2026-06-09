package com.trajectiv.bll.dto.me;

import com.trajectiv.dl.enums.CareerGoal;
import com.trajectiv.dl.enums.ExperienceLevel;

public record UpdateUserProfileCommandBllDto(
        CareerGoal careerGoal,
        String targetRole,
        ExperienceLevel experienceLevel,
        String preferredLanguage
) {
}
