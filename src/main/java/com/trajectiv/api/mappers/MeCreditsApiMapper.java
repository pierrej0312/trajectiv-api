package com.trajectiv.api.mappers;

import com.trajectiv.api.dto.me.credit.MeCreditsApiDto;
import com.trajectiv.bll.dto.credits.UserAiCreditWalletBllDto;
import org.springframework.stereotype.Component;

@Component
public class MeCreditsApiMapper {

    public MeCreditsApiDto toApiDto(
            UserAiCreditWalletBllDto credits
    ) {
        return new MeCreditsApiDto(
                credits.monthlyLimit(),
                credits.used(),
                credits.remaining(),
                credits.periodStart(),
                credits.periodEnd(),
                credits.nextRenewalDate()
        );
    }
}