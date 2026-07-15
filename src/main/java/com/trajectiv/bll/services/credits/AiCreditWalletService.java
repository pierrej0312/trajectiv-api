package com.trajectiv.bll.services.credits;

import com.trajectiv.dl.entities.AiCreditWallet;
import com.trajectiv.bll.dto.me.AiOperationType;

import java.util.UUID;

public interface AiCreditWalletService {
    AiCreditWallet getCurrentWallet(UUID userId);

    AiCreditWallet consume(
            UUID userId,
            AiOperationType operationType
    );
}
