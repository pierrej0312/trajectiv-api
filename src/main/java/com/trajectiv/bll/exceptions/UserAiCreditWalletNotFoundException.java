package com.trajectiv.bll.exceptions;

import java.util.UUID;

public class UserAiCreditWalletNotFoundException extends BusinessException {

    public UserAiCreditWalletNotFoundException(
            UUID userId
    ) {
        super(
                BusinessErrorCode
                        .USER_AI_CREDIT_WALLET_NOT_FOUND,
                "No AI credit wallet was found for user "
                        + userId + "."
        );
    }
}
