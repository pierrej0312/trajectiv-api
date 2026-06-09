package com.trajectiv.api.dto.me;

public record MeCreditsApiDto(
        int monthlyLimit,
        int used,
        int remaining
) {
}
