package com.trajectiv.bll.services.me;

import com.trajectiv.bll.dto.me.MeBllDto;
import com.trajectiv.bll.dto.me.onboarding.MeOnboardingBllDto;
import com.trajectiv.bll.dto.me.workspace.MeWorkspaceBllDto;
import com.trajectiv.bll.exceptions.BusinessErrorCode;
import com.trajectiv.bll.exceptions.UserContextInitializationException;
import com.trajectiv.bll.mappers.AiCreditBllMapper;
import com.trajectiv.bll.mappers.UserBllMapper;
import com.trajectiv.bll.mappers.UserProfileBllMapper;
import com.trajectiv.bll.mappers.billing.UserSubscriptionBllMapper;
import com.trajectiv.bll.services.credits.user.UserAiCreditWalletService;
import com.trajectiv.bll.services.me.sync.UserSyncService;
import com.trajectiv.bll.services.me.workspace.MeWorkspaceService;
import com.trajectiv.bll.services.onboarding.OnboardingService;
import com.trajectiv.bll.services.subscription.user.UserSubscriptionService;
import com.trajectiv.dl.entities.User;
import com.trajectiv.dl.entities.UserProfile;
import com.trajectiv.dl.entities.billing.UserSubscription;
import com.trajectiv.dl.entities.credits.AiCreditWallet;
import com.trajectiv.dl.repositories.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MeServiceImpl implements MeService {

    private final UserSyncService userSyncService;

    private final UserProfileRepository
            userProfileRepository;

    private final UserSubscriptionService
            userSubscriptionService;

    private final UserAiCreditWalletService
            userAiCreditWalletService;

    private final MeWorkspaceService
            meWorkspaceService;

    private final UserBllMapper
            userBllMapper;

    private final UserProfileBllMapper
            userProfileBllMapper;

    private final UserSubscriptionBllMapper
            userSubscriptionBllMapper;

    private final AiCreditBllMapper
            aiCreditBllMapper;

    private final OnboardingService
            onboardingService;

    @Override
    @Transactional
    public MeBllDto getMe(
            Authentication authentication
    ) {
        User user =
                userSyncService.syncFromAuthentication(
                        authentication
                );

        UserProfile profile =
                userProfileRepository
                        .findByUserId(user.getId())
                        .orElseThrow(
                                () ->
                                        new UserContextInitializationException(
                                                BusinessErrorCode
                                                        .USER_PROFILE_NOT_INITIALIZED,
                                                user.getId(),
                                                "user_profile"
                                        )
                        );

        UserSubscription userSubscription =
                userSubscriptionService.getCurrent(
                        user.getId()
                );

        AiCreditWallet wallet =
                userAiCreditWalletService.getCurrent(
                        user.getId()
                );

        MeOnboardingBllDto onboarding =
                onboardingService.buildOnboarding(
                        profile
                );

        List<MeWorkspaceBllDto> workspaces =
                meWorkspaceService.getWorkspaces(
                        user.getId()
                );

        return new MeBllDto(
                userBllMapper.toDto(user),
                userProfileBllMapper.toDto(profile),
                onboarding,
                userSubscriptionBllMapper.toDto(
                        userSubscription,
                        user.getId()
                ),
                aiCreditBllMapper.toDto(wallet),
                workspaces
        );
    }
}