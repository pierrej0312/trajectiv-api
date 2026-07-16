package com.trajectiv.bll.services.credits.user;

import com.trajectiv.bll.dto.billing.EffectiveEntitlementBllDto;
import com.trajectiv.bll.dto.me.AiOperationType;
import com.trajectiv.bll.exceptions.BusinessErrorCode;
import com.trajectiv.bll.exceptions.UserContextInitializationException;
import com.trajectiv.bll.services.credits.policy.AiCreditCostPolicy;
import com.trajectiv.bll.services.credits.policy.AiCreditLimitPolicy;
import com.trajectiv.bll.services.entitlement.user.UserEntitlementService;
import com.trajectiv.dl.entities.credits.AiCreditWallet;
import com.trajectiv.dl.enums.FeatureKey;
import com.trajectiv.dl.repositories.credits.UserAiCreditWalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserAiCreditWalletServiceImpl
        implements UserAiCreditWalletService {

    private final UserAiCreditWalletRepository repository;
    private final UserEntitlementService entitlementService;
    private final AiCreditLimitPolicy limitPolicy;
    private final AiCreditCostPolicy costPolicy;
    private final Clock clock;

    @Override
    @Transactional
    public AiCreditWallet getCurrent(
            UUID userId
    ) {
        AiCreditWallet wallet =
                getWalletForUpdate(userId);

        renewIfNecessary(
                wallet,
                userId
        );

        return wallet;
    }

    @Override
    @Transactional
    public AiCreditWallet consume(
            UUID userId,
            AiOperationType operationType
    ) {
        Objects.requireNonNull(
                operationType,
                "operationType cannot be null."
        );

        AiCreditWallet wallet =
                getWalletForUpdate(userId);

        LocalDate today =
                LocalDate.now(clock);

        renewIfNecessary(
                wallet,
                userId
        );

        wallet.consume(
                costPolicy.costOf(operationType),
                today
        );

        return wallet;
    }

    private AiCreditWallet getWalletForUpdate(
            UUID userId
    ) {
        return repository
                .findByUserIdForUpdate(userId)
                .orElseThrow(
                        () ->
                                new UserContextInitializationException(
                                        BusinessErrorCode
                                                .USER_AI_CREDIT_WALLET_NOT_INITIALIZED,
                                        userId,
                                        "user_ai_credit_wallet"
                                )
                );
    }

    private void renewIfNecessary(
            AiCreditWallet wallet,
            UUID userId
    ) {
        EffectiveEntitlementBllDto entitlement =
                entitlementService.resolveForUser(
                        userId,
                        FeatureKey
                                .AI_CREDITS_MONTHLY_LIMIT
                );

        int monthlyLimit =
                limitPolicy.resolveMonthlyLimit(
                        entitlement
                );

        wallet.renewPeriodIfExpired(
                LocalDate.now(clock),
                monthlyLimit
        );
    }
}