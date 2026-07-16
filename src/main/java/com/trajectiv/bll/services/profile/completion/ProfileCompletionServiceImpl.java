package com.trajectiv.bll.services.profile.completion;

import com.trajectiv.bll.dto.me.onboarding.OnboardingMissingField;
import com.trajectiv.bll.dto.me.profile.ProfileCompletionRequirement;
import com.trajectiv.bll.dto.me.profile.ProfileCompletionResponseBllDto;
import com.trajectiv.bll.dto.me.profile.RecommendedProfileAction;
import com.trajectiv.bll.exceptions.BusinessErrorCode;
import com.trajectiv.bll.exceptions.UserContextInitializationException;
import com.trajectiv.bll.services.me.sync.UserSyncService;
import com.trajectiv.bll.services.onboarding.OnboardingService;
import com.trajectiv.dl.entities.User;
import com.trajectiv.dl.entities.UserProfile;
import com.trajectiv.dl.enums.OnboardingStatus;
import com.trajectiv.dl.enums.file.UserFileKind;
import com.trajectiv.dl.repositories.UserFileRepository;
import com.trajectiv.dl.repositories.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileCompletionServiceImpl implements ProfileCompletionService {

    private static final int TOTAL_REQUIREMENTS = 6;

    private final UserSyncService userSyncService;
    private final OnboardingService onboardingService;
    private final UserProfileRepository userProfileRepository;
    private final UserFileRepository userFileRepository;

    @Override
    @Transactional(readOnly = true)
    public ProfileCompletionResponseBllDto getProfileCompletion(Authentication authentication) {
        User user = userSyncService.syncFromAuthentication(authentication);

        UserProfile profile = userProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new UserContextInitializationException(
                        BusinessErrorCode.USER_PROFILE_NOT_INITIALIZED,
                        user.getId(),
                        "user_profile"
                ));

        List<ProfileCompletionRequirement> missingFields = resolveMissingFields(user, profile);
        List<RecommendedProfileAction> recommendedActions = resolveRecommendedActions(profile, missingFields);
        int completionPercentage = calculateCompletionPercentage(missingFields.size());

        return new ProfileCompletionResponseBllDto(
                completionPercentage,
                missingFields,
                recommendedActions
        );
    }

    private List<ProfileCompletionRequirement> resolveMissingFields(User user, UserProfile profile) {
        List<ProfileCompletionRequirement> missingFields = new ArrayList<>();

        if (isBlank(user.getDisplayName())) {
            missingFields.add(ProfileCompletionRequirement.DISPLAY_NAME);
        }

        for (OnboardingMissingField onboardingMissingField : onboardingService.resolveMissingFields(profile)) {
            missingFields.add(toProfileCompletionRequirement(onboardingMissingField));
        }

        if (profile.getAvatarFile() == null && isBlank(profile.getAvatarUrl())) {
            missingFields.add(ProfileCompletionRequirement.AVATAR);
        }

        boolean hasResume = !userFileRepository.findByOwnerUserIdAndKindAndDeletedAtIsNull(
                user.getId(),
                UserFileKind.RESUME
        ).isEmpty();

        if (!hasResume) {
            missingFields.add(ProfileCompletionRequirement.RESUME);
        }

        return missingFields;
    }

    private List<RecommendedProfileAction> resolveRecommendedActions(
            UserProfile profile,
            List<ProfileCompletionRequirement> missingFields
    ) {
        List<RecommendedProfileAction> actions = new ArrayList<>();

        if (profile.getOnboardingStatus() != OnboardingStatus.COMPLETED) {
            actions.add(RecommendedProfileAction.COMPLETE_ONBOARDING);
        }

        if (missingFields.contains(ProfileCompletionRequirement.AVATAR)) {
            actions.add(RecommendedProfileAction.CREATE_AVATAR);
        }

        if (missingFields.contains(ProfileCompletionRequirement.RESUME)) {
            actions.add(RecommendedProfileAction.UPLOAD_FIRST_RESUME);
            actions.add(RecommendedProfileAction.CREATE_FIRST_RESUME);
        }

        return actions;
    }

    private int calculateCompletionPercentage(int missingCount) {
        int completedCount = TOTAL_REQUIREMENTS - missingCount;
        return Math.round((completedCount * 100f) / TOTAL_REQUIREMENTS);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private ProfileCompletionRequirement toProfileCompletionRequirement(
            OnboardingMissingField missingField
    ) {
        return switch (missingField) {
            case CAREER_GOAL -> ProfileCompletionRequirement.CAREER_GOAL;
            case TARGET_ROLE -> ProfileCompletionRequirement.TARGET_ROLE;
            case EXPERIENCE_LEVEL -> ProfileCompletionRequirement.EXPERIENCE_LEVEL;
        };
    }
}