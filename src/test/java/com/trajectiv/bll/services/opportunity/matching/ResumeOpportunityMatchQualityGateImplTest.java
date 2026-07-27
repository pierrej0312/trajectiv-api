package com.trajectiv.bll.services.opportunity.matching;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trajectiv.bll.exceptions.opportunity.InvalidResumeOpportunityMatchException;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ResumeOpportunityMatchQualityGateImplTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final DefaultResourceLoader resourceLoader =
            new DefaultResourceLoader();
    private final ResumeOpportunityMatchQualityGate gate =
            new ResumeOpportunityMatchQualityGateImpl();

    @Test
    void validFixtureShouldPass() throws Exception {
        JsonNode opportunity = read(
                "classpath:ai/examples/opportunity-analysis.v1.example.json"
        );
        JsonNode match = read(
                "classpath:ai/examples/resume-opportunity-match.v1.example.json"
        );

        assertThatCode(() -> gate.validate(opportunity, match))
                .doesNotThrowAnyException();
    }

    @Test
    void unknownRequirementReferenceShouldFail() throws Exception {
        JsonNode opportunity = read(
                "classpath:ai/examples/opportunity-analysis.v1.example.json"
        );
        JsonNode match = read(
                "classpath:ai/examples/resume-opportunity-match.v1.example.json"
        );

        ((com.fasterxml.jackson.databind.node.ObjectNode)
                match.path("requirementMatches").get(0))
                .put("requirementId", "REQ-999");

        assertThatThrownBy(() -> gate.validate(opportunity, match))
                .isInstanceOf(
                        InvalidResumeOpportunityMatchException.class
                );
    }

    private JsonNode read(String location) throws Exception {
        return objectMapper.readTree(
                resourceLoader
                        .getResource(location)
                        .getInputStream()
        );
    }
}