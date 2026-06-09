package com.trajectiv.api.dto.errors;

import java.time.Instant;

public record ApiErrorResponse(
        String code,
        String message,
        int status,
        Instant timestamp,
        String path
) {
}
