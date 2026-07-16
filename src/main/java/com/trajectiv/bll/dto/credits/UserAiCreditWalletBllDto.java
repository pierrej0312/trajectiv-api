package com.trajectiv.bll.dto.credits;

import java.time.LocalDate;

public record UserAiCreditWalletBllDto(
        int monthlyLimit,
        int used,
        int remaining,
        LocalDate periodStart,
        LocalDate periodEnd,
        LocalDate nextRenewalDate
) {
}
