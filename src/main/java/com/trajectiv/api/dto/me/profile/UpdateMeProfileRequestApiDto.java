package com.trajectiv.api.dto.me.profile;

import com.trajectiv.dl.enums.CareerGoal;
import com.trajectiv.dl.enums.ExperienceLevel;
import com.trajectiv.dl.enums.TargetRoleSource;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record UpdateMeProfileRequestApiDto(
        String displayName,

        CareerGoal careerGoal,

        UUID targetRoleId,

        @Size(max = 180)
        String targetRoleLabel,

        TargetRoleSource targetRoleSource,

        ExperienceLevel experienceLevel,

        @Size(min = 2, max = 10)
        String preferredLanguage
) {
}