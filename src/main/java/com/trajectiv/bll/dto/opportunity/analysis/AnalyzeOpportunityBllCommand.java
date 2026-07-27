package com.trajectiv.bll.dto.opportunity.analysis;

import java.util.Objects;

public record AnalyzeOpportunityBllCommand(String description) {
    public AnalyzeOpportunityBllCommand {
        description = Objects.requireNonNull(description, "description is required").trim();
        if (description.length() < 50) {
            throw new IllegalArgumentException("description must contain at least 50 characters");
        }
        if (description.length() > 60_000) {
            throw new IllegalArgumentException("description must not exceed 60000 characters");
        }
    }
}