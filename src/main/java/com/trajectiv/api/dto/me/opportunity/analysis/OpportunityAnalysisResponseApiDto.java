package com.trajectiv.api.dto.me.opportunity.analysis;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.UUID;

public record OpportunityAnalysisResponseApiDto(
        UUID analysisId,
        String schemaVersion,
        String promptVersion,
        String provider,
        String model,
        long durationMillis,
        JsonNode artifact
) {
    public OpportunityAnalysisResponseApiDto {
        artifact = artifact == null ? null : artifact.deepCopy();
    }

    @Override
    public JsonNode artifact() {
        return artifact == null ? null : artifact.deepCopy();
    }
}
