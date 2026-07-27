package com.trajectiv.bll.models.careerai;

import java.util.Objects;

public record AiUseCaseDefinition(
        AiUseCase useCase,
        String promptId,
        String promptVersion,
        String promptResource,
        String schemaVersion,
        String schemaResource,
        String fixtureResource
) {
    public AiUseCaseDefinition {
        Objects.requireNonNull(useCase, "useCase is required");
        Objects.requireNonNull(promptId, "promptId is required");
        Objects.requireNonNull(promptVersion, "promptVersion is required");
        Objects.requireNonNull(promptResource, "promptResource is required");
        Objects.requireNonNull(schemaVersion, "schemaVersion is required");
        Objects.requireNonNull(schemaResource, "schemaResource is required");
        Objects.requireNonNull(fixtureResource, "fixtureResource is required");
    }

    public static AiUseCaseDefinition forUseCase(AiUseCase useCase) {
        return switch (Objects.requireNonNull(useCase, "useCase is required")) {
            case RESUME_INTELLIGENCE -> new AiUseCaseDefinition(
                    useCase,
                    "RESUME_INTELLIGENCE",
                    "resume-intelligence.v1",
                    "classpath:ai/prompts/resume-intelligence.v1.txt",
                    "resume-analysis.v1",
                    "classpath:ai/contracts/resume-analysis.v1.schema.json",
                    "classpath:ai/examples/resume-analysis.v1.example.json"
            );
            case OPPORTUNITY_EXTRACT -> new AiUseCaseDefinition(
                    useCase,
                    "OPPORTUNITY_EXTRACT",
                    "opportunity-extract.v1",
                    "classpath:ai/prompts/opportunity-extract.v1.txt",
                    "opportunity-analysis.v1",
                    "classpath:ai/contracts/opportunity-analysis.v1.schema.json",
                    "classpath:ai/examples/opportunity-analysis.v1.example.json"
            );
            case RESUME_OPPORTUNITY_MATCH -> new AiUseCaseDefinition(
                    useCase,
                    "RESUME_OPPORTUNITY_MATCH",
                    "resume-opportunity-match.v1",
                    "classpath:ai/prompts/resume-opportunity-match.v1.txt",
                    "resume-opportunity-match.v1",
                    "classpath:ai/contracts/resume-opportunity-match.v1.schema.json",
                    "classpath:ai/examples/resume-opportunity-match.v1.example.json"
            );
        };
    }
}