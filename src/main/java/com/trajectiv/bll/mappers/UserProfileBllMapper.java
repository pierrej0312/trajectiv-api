package com.trajectiv.bll.mappers;

import com.trajectiv.bll.dto.me.UserProfileBllDto;
import com.trajectiv.dl.entities.UserProfile;
import org.springframework.stereotype.Component;

@Component
public class UserProfileBllMapper {

    public UserProfileBllDto toDto(UserProfile profile) {
        return new UserProfileBllDto(
                profile.getId(),
                profile.getAvatarUrl(),
                profile.getCareerGoal(),
                profile.getTargetRole(),
                profile.getExperienceLevel(),
                profile.getPreferredLanguage()
        );
    }
}
