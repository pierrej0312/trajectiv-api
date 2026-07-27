package com.trajectiv.bll.services.opportunity.matching;

import com.fasterxml.jackson.databind.JsonNode;

public interface ResumeOpportunityMatchQualityGate {

    void validate(JsonNode opportunityArtifact, JsonNode matchArtifact);
}