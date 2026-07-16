package com.trajectiv.bll.services.profile.completion;

import com.trajectiv.bll.dto.me.profile.ProfileCompletionResponseBllDto;
import org.springframework.security.core.Authentication;

public interface ProfileCompletionService {
    ProfileCompletionResponseBllDto getProfileCompletion(Authentication authentication);
}
