package com.trajectiv.bll.services.careerai.schema;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trajectiv.bll.models.careerai.AiUseCase;
import com.trajectiv.bll.models.careerai.AiUseCaseDefinition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.core.io.DefaultResourceLoader;

import static org.assertj.core.api.Assertions.assertThat;

class JsonSchemaAiArtifactValidatorTest {

    private final DefaultResourceLoader resourceLoader =
            new DefaultResourceLoader();

    private ObjectMapper objectMapper;
    private AiSchemaRegistry schemaRegistry;
    private AiArtifactValidator validator;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        schemaRegistry = new AiSchemaRegistryImpl(
                resourceLoader,
                objectMapper
        );
        validator = new AiArtifactValidatorImpl();
    }

    @ParameterizedTest
    @EnumSource(AiUseCase.class)
    void exampleShouldConformToItsSchema(
            AiUseCase useCase
    ) throws Exception {
        String fixtureLocation = AiUseCaseDefinition
                .forUseCase(useCase)
                .fixtureResource();

        JsonNode fixture = objectMapper.readTree(
                resourceLoader
                        .getResource(fixtureLocation)
                        .getInputStream()
        );

        var result = validator.validate(
                schemaRegistry.getSchemaVersion(useCase),
                schemaRegistry.getSchema(useCase),
                fixture
        );

        assertThat(result.valid()).isTrue();
        assertThat(result.errors()).isEmpty();
    }

    @Test
    void invalidArtifactShouldBeRejected() throws Exception {
        JsonNode fixture = objectMapper.readTree(
                resourceLoader
                        .getResource(
                                "classpath:ai/examples/resume-analysis.v1.example.json"
                        )
                        .getInputStream()
        );

        ((com.fasterxml.jackson.databind.node.ObjectNode) fixture)
                .remove("schemaVersion");

        var result = validator.validate(
                schemaRegistry.getSchemaVersion(
                        AiUseCase.RESUME_INTELLIGENCE
                ),
                schemaRegistry.getSchema(
                        AiUseCase.RESUME_INTELLIGENCE
                ),
                fixture
        );

        assertThat(result.valid()).isFalse();
        assertThat(result.errors()).isNotEmpty();
    }
}