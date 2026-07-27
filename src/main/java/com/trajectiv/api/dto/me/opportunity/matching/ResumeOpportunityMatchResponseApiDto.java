package com.trajectiv.api.dto.me.opportunity.matching;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.UUID;

public record ResumeOpportunityMatchResponseApiDto(
        UUID matchId,
        String schemaVersion,
        String promptVersion,
        String provider,
        String model,
        long durationMillis,
        JsonNode artifact
) {
    public ResumeOpportunityMatchResponseApiDto {
        artifact = artifact == null ? null : artifact.deepCopy();
    }

    @Override
    public JsonNode artifact() {
        return artifact == null ? null : artifact.deepCopy();
    }
}