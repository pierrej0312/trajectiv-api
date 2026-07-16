package com.trajectiv.bll.services.profile;

import com.trajectiv.bll.dto.me.profile.UpdateUserProfileCommandBllDto;
import com.trajectiv.bll.dto.me.profile.UpdatedUserProfileBllDto;
import org.springframework.security.core.Authentication;

public interface UserProfileService {

    UpdatedUserProfileBllDto updateCurrentUserProfile(
            Authentication authentication,
            UpdateUserProfileCommandBllDto command
    );
}
