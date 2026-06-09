package com.trajectiv.bll.services.onboarding;

import com.trajectiv.bll.dto.me.MeOnboardingBllDto;
import com.trajectiv.bll.dto.me.OnboardingMissingField;
import com.trajectiv.bll.exceptions.BusinessErrorCode;
import com.trajectiv.bll.exceptions.OnboardingRequiredFieldsMissingException;
import com.trajectiv.bll.exceptions.UserContextInitializationException;
import com.trajectiv.bll.services.me.UserSyncService;
import com.trajectiv.dl.entities.User;
import com.trajectiv.dl.entities.UserProfile;
import com.trajectiv.dl.enums.OnboardingStatus;
import com.trajectiv.dl.repositories.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OnboardingServiceImpl implements OnboardingService {

    private final UserSyncService userSyncService;
    private final UserProfileRepository userProfileRepository;

    @Override
    public MeOnboardingBllDto buildOnboarding(UserProfile profile) {
        return new MeOnboardingBllDto(
                profile.getOnboardingStatus(),
                profile.getOnboardingCompletedAt(),
                resolveMissingFields(profile)
        );
    }

    @Override
    public List<OnboardingMissingField> resolveMissingFields(UserProfile profile) {
        if (profile.getOnboardingStatus() == OnboardingStatus.COMPLETED) {
            return List.of();
        }

        List<OnboardingMissingField> missingFields = new ArrayList<>();

        if (profile.getCareerGoal() == null) {
            missingFields.add(OnboardingMissingField.CAREER_GOAL);
        }

        if (profile.getTargetRole() == null || profile.getTargetRole().isBlank()) {
            missingFields.add(OnboardingMissingField.TARGET_ROLE);
        }

        if (profile.getExperienceLevel() == null) {
            missingFields.add(OnboardingMissingField.EXPERIENCE_LEVEL);
        }

        return missingFields;
    }

    @Override
    @Transactional
    public MeOnboardingBllDto completeCurrentUserOnboarding(Authentication authentication) {
        User user = userSyncService.syncFromAuthentication(authentication);

        UserProfile profile = userProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new UserContextInitializationException(
                        BusinessErrorCode.USER_PROFILE_NOT_INITIALIZED,
                        user.getId(),
                        "user_profile"
                ));

        List<OnboardingMissingField> missingFields = resolveMissingFields(profile);

        if (!missingFields.isEmpty()) {
            throw new OnboardingRequiredFieldsMissingException(missingFields);
        }

        profile.completeOnboarding();

        UserProfile savedProfile = userProfileRepository.save(profile);

        return buildOnboarding(savedProfile);
    }
}
