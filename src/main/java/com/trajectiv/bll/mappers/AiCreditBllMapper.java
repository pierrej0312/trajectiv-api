package com.trajectiv.bll.mappers;

import com.trajectiv.bll.dto.credits.UserAiCreditWalletBllDto;
import com.trajectiv.dl.entities.credits.AiCreditWallet;
import org.springframework.stereotype.Component;

@Component
public class AiCreditBllMapper {

    public UserAiCreditWalletBllDto toDto(AiCreditWallet wallet) {
        return new UserAiCreditWalletBllDto(
                wallet.getMonthlyLimit(),
                wallet.getUsedThisPeriod(),
                wallet.getRemaining(),
                wallet.getPeriodStart(),
                wallet.getPeriodEnd(),
                wallet.getNextRenewalDate()
        );
    }
}
