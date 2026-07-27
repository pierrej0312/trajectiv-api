package com.trajectiv.bll.dto.resume.analysis;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.UUID;

public record ResumeAnalysisBllDto(
        UUID analysisId,
        String schemaVersion,
        String promptVersion,
        String provider,
        String model,
        long durationMillis,
        JsonNode artifact
) {
    public ResumeAnalysisBllDto {
        artifact = artifact == null ? null : artifact.deepCopy();
    }

    @Override
    public JsonNode artifact() {
        return artifact == null ? null : artifact.deepCopy();
    }
}