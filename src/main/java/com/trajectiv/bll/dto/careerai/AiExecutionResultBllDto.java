package com.trajectiv.bll.dto.careerai;

import com.fasterxml.jackson.databind.JsonNode;
import com.trajectiv.bll.models.careerai.AiUseCase;

import java.util.UUID;

public record AiExecutionResultBllDto(
        UUID executionId,
        AiUseCase useCase,
        String schemaVersion,
        String promptVersion,
        String provider,
        String model,
        long durationMillis,
        JsonNode artifact
) {
    public AiExecutionResultBllDto {
        artifact = artifact == null ? null : artifact.deepCopy();
    }

    @Override
    public JsonNode artifact() {
        return artifact == null ? null : artifact.deepCopy();
    }
}