package com.trajectiv.api.dto.me.opportunity.analysis;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AnalyzeOpportunityRequestApiDto(
        @NotBlank
        @Size(min = 50, max = 60_000)
        String description
) {
}