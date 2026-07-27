package com.trajectiv.bll.models.careerai;

import java.util.Objects;

public record AiProviderResponse(
        String provider,
        String model,
        String rawOutput,
        long durationMillis
) {
    public AiProviderResponse {
        provider = Objects.requireNonNull(provider, "provider is required");
        model = Objects.requireNonNull(model, "model is required");
        rawOutput = Objects.requireNonNull(rawOutput, "rawOutput is required");
        if (durationMillis < 0) {
            throw new IllegalArgumentException("durationMillis must not be negative");
        }
    }
}