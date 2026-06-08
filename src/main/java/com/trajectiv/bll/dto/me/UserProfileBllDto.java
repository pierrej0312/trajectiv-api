package com.trajectiv.bll.dto.me;

import com.trajectiv.dl.enums.CareerGoal;
import com.trajectiv.dl.enums.ExperienceLevel;

import java.util.UUID;

public record UserProfileBllDto(
        UUID id,
        String avatarUrl,
        CareerGoal careerGoal,
        String targetRole,
        ExperienceLevel experienceLevel,
        String preferredLanguage
) {
}
