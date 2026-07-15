package com.trajectiv.bll.services.credits;

import com.trajectiv.bll.exceptions.BusinessErrorCode;
import com.trajectiv.bll.exceptions.UserContextInitializationException;
import com.trajectiv.bll.services.credits.policy.AiCreditCostPolicy;
import com.trajectiv.bll.services.subscription.SubscriptionService;
import com.trajectiv.dl.entities.AiCreditWallet;
import com.trajectiv.bll.dto.me.AiOperationType;
import com.trajectiv.dl.repositories.AiCreditWalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AiCreditWalletServiceImpl
        implements AiCreditWalletService {

    private final AiCreditWalletRepository walletRepository;
    private final AiCreditCostPolicy creditCostPolicy;
    private final SubscriptionService subscriptionService;
    private final Clock clock;

    @Override
    @Transactional
    public AiCreditWallet getCurrentWallet(UUID userId) {
        AiCreditWallet wallet = walletRepository
                .findByUserIdForUpdate(userId)
                .orElseThrow(() -> walletNotFound(userId));

        LocalDate today = LocalDate.now(clock);
        int monthlyLimit =
                subscriptionService.resolveAiMonthlyLimit(userId);

        wallet.renewPeriodIfExpired(today, monthlyLimit);

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

        AiCreditWallet wallet = walletRepository
                .findByUserIdForUpdate(userId)
                .orElseThrow(() -> walletNotFound(userId));

        LocalDate today = LocalDate.now(clock);

        int monthlyLimit =
                subscriptionService.resolveAiMonthlyLimit(userId);

        wallet.renewPeriodIfExpired(today, monthlyLimit);

        int cost = creditCostPolicy.costOf(operationType);

        wallet.consume(cost, today);

        return wallet;
    }

    private RuntimeException walletNotFound(UUID userId) {
        return new UserContextInitializationException(
                BusinessErrorCode.USER_AI_CREDIT_WALLET_NOT_INITIALIZED,
                userId,
                "ai_credit_wallet"
        );
    }
}
