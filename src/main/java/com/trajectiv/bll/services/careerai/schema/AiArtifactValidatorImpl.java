package com.trajectiv.bll.services.careerai.schema;

import com.fasterxml.jackson.databind.JsonNode;
import com.networknt.schema.InputFormat;
import com.networknt.schema.Schema;
import com.networknt.schema.SchemaRegistry;
import com.networknt.schema.SpecificationVersion;
import com.trajectiv.bll.models.careerai.ArtifactValidationResult;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class AiArtifactValidatorImpl implements AiArtifactValidator {

    private final SchemaRegistry schemaRegistry =
            SchemaRegistry.withDefaultDialect(SpecificationVersion.DRAFT_2020_12);

    private final ConcurrentMap<String, Schema> compiledSchemas = new ConcurrentHashMap<>();

    @Override
    public ArtifactValidationResult validate(
            String schemaVersion,
            JsonNode schema,
            JsonNode artifact
    ) {
        Schema compiledSchema = compiledSchemas.computeIfAbsent(
                schemaVersion,
                ignored -> {
                    Schema value = schemaRegistry.getSchema(schema);
                    value.initializeValidators();
                    return value;
                }
        );

        List<String> errors = compiledSchema
                .validate(
                        artifact.toString(),
                        InputFormat.JSON,
                        executionContext -> executionContext.executionConfig(
                                executionConfig -> executionConfig.formatAssertionsEnabled(true)
                        )
                )
                .stream()
                .map(Object::toString)
                .sorted()
                .toList();

        return errors.isEmpty()
                ? ArtifactValidationResult.success()
                : ArtifactValidationResult.failure(errors);
    }
}
