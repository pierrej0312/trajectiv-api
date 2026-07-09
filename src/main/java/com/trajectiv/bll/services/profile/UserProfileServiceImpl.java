package com.trajectiv.bll.services.profile;

import com.trajectiv.bll.dto.me.UpdateUserProfileCommandBllDto;
import com.trajectiv.bll.dto.me.UpdatedUserProfileBllDto;
import com.trajectiv.bll.exceptions.BusinessErrorCode;
import com.trajectiv.bll.exceptions.UserContextInitializationException;
import com.trajectiv.bll.mappers.UserProfileBllMapper;
import com.trajectiv.bll.services.me.UserSyncService;
import com.trajectiv.bll.services.onboarding.OnboardingService;
import com.trajectiv.dl.entities.JobRole;
import com.trajectiv.dl.entities.User;
import com.trajectiv.dl.entities.UserProfile;
import com.trajectiv.dl.enums.TargetRoleSource;
import com.trajectiv.dl.repositories.JobRoleRepository;
import com.trajectiv.dl.repositories.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserSyncService userSyncService;
    private final UserProfileRepository userProfileRepository;
    private final UserProfileBllMapper userProfileBllMapper;
    private final OnboardingService onboardingService;
    private final JobRoleRepository jobRoleRepository;

    @Override
    @Transactional
    public UpdatedUserProfileBllDto updateCurrentUserProfile(
            Authentication authentication,
            UpdateUserProfileCommandBllDto command
    ) {
        User user = userSyncService.syncFromAuthentication(authentication);

        UserProfile profile = userProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new UserContextInitializationException(
                        BusinessErrorCode.USER_PROFILE_NOT_INITIALIZED,
                        user.getId(),
                        "user_profile"
                ));

        JobRole targetRole = resolveTargetRole(command);

        profile.updateProfile(
                command.careerGoal(),
                targetRole,
                command.targetRoleLabel(),
                resolveTargetRoleSource(command, targetRole),
                command.experienceLevel(),
                command.preferredLanguage()
        );

        if (command.displayName() != null) {
            user.setDisplayName(command.displayName());
        }

        UserProfile savedProfile = userProfileRepository.save(profile);

        return new UpdatedUserProfileBllDto(
                userProfileBllMapper.toDto(savedProfile),
                onboardingService.buildOnboarding(savedProfile)
        );
    }

    private JobRole resolveTargetRole(UpdateUserProfileCommandBllDto command) {
        if (command.targetRoleId() == null) {
            return null;
        }

        return jobRoleRepository.findByIdAndActiveTrue(command.targetRoleId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Selected target role does not exist or is inactive."
                ));
    }

    private TargetRoleSource resolveTargetRoleSource(
            UpdateUserProfileCommandBllDto command,
            JobRole targetRole
    ) {
        if (targetRole != null) {
            return TargetRoleSource.CATALOG;
        }

        if (command.targetRoleLabel() != null && !command.targetRoleLabel().isBlank()) {
            return command.targetRoleSource() != null
                    ? command.targetRoleSource()
                    : TargetRoleSource.CUSTOM;
        }

        return null;
    }
}
