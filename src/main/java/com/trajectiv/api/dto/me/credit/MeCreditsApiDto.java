package com.trajectiv.api.dto.me.credit;

import java.time.LocalDate;

public record MeCreditsApiDto(
        int monthlyLimit,
        int used,
        int remaining,
        LocalDate periodStart,
        LocalDate periodEnd,
        LocalDate nextRenewalDate
) {
}
