package com.trajectiv.bll.services.credits.user;

import com.trajectiv.bll.dto.me.AiOperationType;
import com.trajectiv.dl.entities.credits.AiCreditWallet;

import java.util.UUID;

public interface UserAiCreditWalletService {

    AiCreditWallet getCurrent(
            UUID userId
    );

    AiCreditWallet consume(
            UUID userId,
            AiOperationType operationType
    );
}