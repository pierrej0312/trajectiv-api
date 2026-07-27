package com.trajectiv.api.dto.errors;

public record ApiFieldError(
        String field,
        String message
) {
}