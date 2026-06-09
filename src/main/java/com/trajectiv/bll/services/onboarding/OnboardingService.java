package com.trajectiv.bll.services.onboarding;

import com.trajectiv.bll.dto.me.MeOnboardingBllDto;
import com.trajectiv.bll.dto.me.OnboardingMissingField;
import com.trajectiv.dl.entities.UserProfile;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface OnboardingService {

    MeOnboardingBllDto buildOnboarding(UserProfile profile);

    List<OnboardingMissingField> resolveMissingFields(UserProfile profile);

    MeOnboardingBllDto completeCurrentUserOnboarding(Authentication authentication);
}
