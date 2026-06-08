package com.trajectiv.bll.dto.me;

public record AiCreditWalletBllDto(
        int monthlyLimit,
        int used,
        int remaining
) {
}
