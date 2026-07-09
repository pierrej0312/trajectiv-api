package com.trajectiv.bll.services.profile;

import com.trajectiv.bll.dto.me.ProfileCompletionResponseBllDto;
import org.springframework.security.core.Authentication;

public interface ProfileCompletionService {
    ProfileCompletionResponseBllDto getProfileCompletion(Authentication authentication);
}
