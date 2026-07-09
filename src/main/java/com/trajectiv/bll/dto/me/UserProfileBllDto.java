package com.trajectiv.bll.dto.me;

import com.trajectiv.dl.enums.CareerGoal;
import com.trajectiv.dl.enums.ExperienceLevel;
import com.trajectiv.dl.enums.TargetRoleSource;

import java.util.UUID;

public record UserProfileBllDto(
        UUID id,
        String avatarUrl,
        CareerGoal careerGoal,
        UUID targetRoleId,
        String targetRoleLabel,
        TargetRoleSource targetRoleSource,
        ExperienceLevel experienceLevel,
        String preferredLanguage
) {
}