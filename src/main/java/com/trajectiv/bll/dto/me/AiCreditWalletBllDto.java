package com.trajectiv.bll.dto.me;

import java.time.LocalDate;

public record AiCreditWalletBllDto(
        int monthlyLimit,
        int used,
        int remaining,
        LocalDate periodStart,
        LocalDate periodEnd,
        LocalDate nextRenewalDate
) {
}
