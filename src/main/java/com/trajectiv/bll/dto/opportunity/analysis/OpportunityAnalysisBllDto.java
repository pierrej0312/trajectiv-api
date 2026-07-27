package com.trajectiv.bll.dto.opportunity.analysis;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.UUID;

public record OpportunityAnalysisBllDto(
        UUID analysisId,
        String schemaVersion,
        String promptVersion,
        String provider,
        String model,
        long durationMillis,
        JsonNode artifact
) {
    public OpportunityAnalysisBllDto {
        artifact = artifact == null ? null : artifact.deepCopy();
    }

    @Override
    public JsonNode artifact() {
        return artifact == null ? null : artifact.deepCopy();
    }
}