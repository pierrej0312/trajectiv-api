package com.trajectiv.bll.mappers;

import com.trajectiv.bll.dto.me.UserProfileBllDto;
import com.trajectiv.dl.entities.JobRole;
import com.trajectiv.dl.entities.UserProfile;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserProfileBllMapper {

    public UserProfileBllDto toDto(UserProfile profile) {
        JobRole targetRole = profile.getTargetRole();

        return new UserProfileBllDto(
                profile.getId(),
                profile.getAvatarUrl(),
                profile.getCareerGoal(),
                targetRole != null ? targetRole.getId() : null,
                profile.getResolvedTargetRoleLabel(),
                profile.getTargetRoleSource(),
                profile.getExperienceLevel(),
                profile.getPreferredLanguage()
        );
    }
}