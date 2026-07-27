package com.trajectiv.bll.dto.opportunity.matching;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.UUID;

public record ResumeOpportunityMatchBllDto(
        UUID matchId,
        String schemaVersion,
        String promptVersion,
        String provider,
        String model,
        long durationMillis,
        JsonNode artifact
) {
    public ResumeOpportunityMatchBllDto {
        artifact = artifact == null ? null : artifact.deepCopy();
    }

    @Override
    public JsonNode artifact() {
        return artifact == null ? null : artifact.deepCopy();
    }
}