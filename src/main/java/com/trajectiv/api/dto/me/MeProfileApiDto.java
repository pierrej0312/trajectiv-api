package com.trajectiv.api.dto.me;

import com.trajectiv.dl.enums.CareerGoal;
import com.trajectiv.dl.enums.ExperienceLevel;
import com.trajectiv.dl.enums.TargetRoleSource;

import java.util.UUID;

public record MeProfileApiDto(
        CareerGoal careerGoal,
        UUID targetRoleId,
        String targetRoleLabel,
        TargetRoleSource targetRoleSource,
        ExperienceLevel experienceLevel,
        String preferredLanguage
) {
}