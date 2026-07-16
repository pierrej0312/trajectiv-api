package com.trajectiv.api.mappers;

import com.trajectiv.api.dto.me.profile.MeProfileApiDto;
import com.trajectiv.api.dto.me.profile.UpdateMeProfileRequestApiDto;
import com.trajectiv.bll.dto.me.profile.UpdateUserProfileCommandBllDto;
import com.trajectiv.bll.dto.me.profile.UserProfileBllDto;
import org.springframework.stereotype.Component;

@Component
public class MeProfileApiMapper {

    public MeProfileApiDto toApiDto(
            UserProfileBllDto profile
    ) {
        return new MeProfileApiDto(
                profile.careerGoal(),
                profile.targetRoleId(),
                profile.targetRoleLabel(),
                profile.targetRoleSource(),
                profile.experienceLevel(),
                profile.preferredLanguage()
        );
    }

    public UpdateUserProfileCommandBllDto toBllCommand(
            UpdateMeProfileRequestApiDto request
    ) {
        return new UpdateUserProfileCommandBllDto(
                request.displayName(),
                request.careerGoal(),
                request.targetRoleId(),
                request.targetRoleLabel(),
                request.targetRoleSource(),
                request.experienceLevel(),
                request.preferredLanguage()
        );
    }
}