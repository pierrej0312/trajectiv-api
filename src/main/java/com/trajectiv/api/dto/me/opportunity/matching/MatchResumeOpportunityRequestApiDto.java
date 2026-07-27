package com.trajectiv.api.dto.me.opportunity.matching;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record MatchResumeOpportunityRequestApiDto(
        @NotNull UUID resumeAnalysisId,
        @NotNull JsonNode resumeAnalysis,
        @NotNull UUID opportunityAnalysisId,
        @NotNull JsonNode opportunityAnalysis
) {
    public MatchResumeOpportunityRequestApiDto {
        resumeAnalysis = resumeAnalysis == null ? null : resumeAnalysis.deepCopy();
        opportunityAnalysis = opportunityAnalysis == null ? null : opportunityAnalysis.deepCopy();
    }

    @Override
    public JsonNode resumeAnalysis() {
        return resumeAnalysis == null ? null : resumeAnalysis.deepCopy();
    }

    @Override
    public JsonNode opportunityAnalysis() {
        return opportunityAnalysis == null ? null : opportunityAnalysis.deepCopy();
    }
}