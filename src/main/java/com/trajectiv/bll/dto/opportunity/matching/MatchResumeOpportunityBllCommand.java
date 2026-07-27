package com.trajectiv.bll.dto.opportunity.matching;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Objects;
import java.util.UUID;

public record MatchResumeOpportunityBllCommand(
        UUID resumeAnalysisId,
        JsonNode resumeAnalysis,
        UUID opportunityAnalysisId,
        JsonNode opportunityAnalysis
) {
    public MatchResumeOpportunityBllCommand {
        Objects.requireNonNull(resumeAnalysisId, "resumeAnalysisId is required");
        Objects.requireNonNull(opportunityAnalysisId, "opportunityAnalysisId is required");
        resumeAnalysis = Objects.requireNonNull(resumeAnalysis, "resumeAnalysis is required").deepCopy();
        opportunityAnalysis = Objects.requireNonNull(opportunityAnalysis, "opportunityAnalysis is required").deepCopy();
    }

    @Override
    public JsonNode resumeAnalysis() {
        return resumeAnalysis.deepCopy();
    }

    @Override
    public JsonNode opportunityAnalysis() {
        return opportunityAnalysis.deepCopy();
    }
}
