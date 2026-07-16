package com.trajectiv.bll.services.credits.user.initialization;

import com.trajectiv.dl.entities.User;
import com.trajectiv.dl.entities.credits.AiCreditWallet;

public interface UserAiCreditWalletInitializer {

    AiCreditWallet createDefaultIfMissing(
            User user
    );
}
