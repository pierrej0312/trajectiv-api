package com.trajectiv.bll.mappers;

import com.trajectiv.bll.dto.me.AiCreditWalletBllDto;
import com.trajectiv.dl.entities.AiCreditWallet;
import org.springframework.stereotype.Component;

@Component
public class AiCreditBllMapper {

    public AiCreditWalletBllDto toDto(AiCreditWallet wallet) {
        return new AiCreditWalletBllDto(
                wallet.getMonthlyLimit(),
                wallet.getUsedThisPeriod(),
                wallet.getRemaining(),
                wallet.getPeriodStart(),
                wallet.getPeriodEnd(),
                wallet.getNextRenewalDate()
        );
    }
}
