package com.trajectiv.bll.services.credits.user.initialization;

import com.trajectiv.dl.entities.User;
import com.trajectiv.dl.entities.credits.AiCreditWallet;
import com.trajectiv.dl.repositories.credits.UserAiCreditWalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserAiCreditWalletInitializerImpl
        implements UserAiCreditWalletInitializer {

    private final UserAiCreditWalletRepository walletRepository;

    @Override
    @Transactional
    public AiCreditWallet createDefaultIfMissing(
            User user
    ) {
        Objects.requireNonNull(
                user,
                "user cannot be null."
        );

        if (user.getId() == null) {
            throw new IllegalArgumentException(
                    "User must be persisted before initializing its AI credit wallet."
            );
        }

        return walletRepository
                .findByUserId(user.getId())
                .orElseGet(
                        () -> walletRepository.save(
                                AiCreditWallet.createFreeDefault(
                                        user
                                )
                        )
                );
    }
}