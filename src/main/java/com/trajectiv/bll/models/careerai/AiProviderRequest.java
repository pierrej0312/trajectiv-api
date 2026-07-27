package com.trajectiv.bll.models.careerai;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Objects;

public record AiProviderRequest(
        AiUseCase useCase,
        String systemPrompt,
        String userPrompt,
        String expectedSchemaVersion,
        JsonNode responseFormatSchema
) {
    public AiProviderRequest {
        Objects.requireNonNull(useCase, "useCase is required");
        systemPrompt = Objects.requireNonNull(systemPrompt, "systemPrompt is required");
        userPrompt = Objects.requireNonNull(userPrompt, "userPrompt is required");
        expectedSchemaVersion = Objects.requireNonNull(
                expectedSchemaVersion,
                "expectedSchemaVersion is required"
        );
        responseFormatSchema = Objects.requireNonNull(
                responseFormatSchema,
                "responseFormatSchema is required"
        ).deepCopy();
    }

    @Override
    public JsonNode responseFormatSchema() {
        return responseFormatSchema.deepCopy();
    }
}