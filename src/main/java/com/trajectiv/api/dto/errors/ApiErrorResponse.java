package com.trajectiv.api.dto.errors;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ApiErrorResponse(
        String code,
        String message,
        int status,
        Instant timestamp,
        String path,
        List<ApiFieldError> violations
) {
    public ApiErrorResponse {
        violations = List.copyOf(
                violations == null ? List.of() : violations
        );
    }

    public ApiErrorResponse(
            String code,
            String message,
            int status,
            Instant timestamp,
            String path
    ) {
        this(
                code,
                message,
                status,
                timestamp,
                path,
                List.of()
        );
    }
}