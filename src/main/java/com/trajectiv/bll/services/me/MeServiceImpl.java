package com.trajectiv.bll.services.me;

import com.trajectiv.bll.dto.me.MeBllDto;
import com.trajectiv.bll.dto.me.MeOnboardingBllDto;
import com.trajectiv.bll.exceptions.BusinessErrorCode;
import com.trajectiv.bll.exceptions.UserContextInitializationException;
import com.trajectiv.bll.mappers.AiCreditBllMapper;
import com.trajectiv.bll.mappers.SubscriptionBllMapper;
import com.trajectiv.bll.mappers.UserBllMapper;
import com.trajectiv.bll.mappers.UserProfileBllMapper;
import com.trajectiv.bll.services.credits.AiCreditWalletService;
import com.trajectiv.bll.services.onboarding.OnboardingService;
import com.trajectiv.bll.services.subscription.SubscriptionService;
import com.trajectiv.dl.entities.AiCreditWallet;
import com.trajectiv.dl.entities.Subscription;
import com.trajectiv.dl.entities.User;
import com.trajectiv.dl.entities.UserProfile;
import com.trajectiv.dl.repositories.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MeServiceImpl implements MeService {

    private final UserSyncService userSyncService;

    private final UserProfileRepository userProfileRepository;
    private final SubscriptionService subscriptionService;
    private final AiCreditWalletService aiCreditWalletService;

    private final UserBllMapper userBllMapper;
    private final UserProfileBllMapper userProfileBllMapper;
    private final SubscriptionBllMapper subscriptionBllMapper;
    private final AiCreditBllMapper aiCreditBllMapper;

    private final OnboardingService onboardingService;

    @Override
    @Transactional
    public MeBllDto getMe(Authentication authentication) {
        User user = userSyncService.syncFromAuthentication(authentication);

        UserProfile profile = userProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new UserContextInitializationException(
                        BusinessErrorCode.USER_PROFILE_NOT_INITIALIZED,
                        user.getId(),
                        "user_profile"
                ));

        Subscription subscription =
                subscriptionService.getCurrentSubscription(user.getId());

        AiCreditWallet wallet =
                aiCreditWalletService.getCurrentWallet(user.getId());

        MeOnboardingBllDto onboarding =
                onboardingService.buildOnboarding(profile);

        return new MeBllDto(
                userBllMapper.toDto(user),
                userProfileBllMapper.toDto(profile),
                onboarding,
                subscriptionBllMapper.toDto(subscription),
                aiCreditBllMapper.toDto(wallet)
        );
    }
}