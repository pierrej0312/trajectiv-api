package com.trajectiv.bll.services.careerai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trajectiv.bll.dto.careerai.AiExecutionCommandBllDto;
import com.trajectiv.bll.dto.careerai.AiExecutionResultBllDto;
import com.trajectiv.bll.exceptions.careerai.AiArtifactValidationException;
import com.trajectiv.bll.exceptions.careerai.AiPromptRenderingException;
import com.trajectiv.bll.exceptions.careerai.AiProviderException;
import com.trajectiv.bll.models.careerai.AiProviderRequest;
import com.trajectiv.bll.models.careerai.AiProviderResponse;
import com.trajectiv.bll.models.careerai.ArtifactValidationResult;
import com.trajectiv.bll.models.careerai.VersionedPrompt;
import com.trajectiv.bll.services.careerai.prompt.AiPromptRegistry;
import com.trajectiv.bll.services.careerai.provider.AiProvider;
import com.trajectiv.bll.services.careerai.schema.AiArtifactValidator;
import com.trajectiv.bll.services.careerai.schema.AiSchemaRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AiExecutionServiceImpl implements AiExecutionService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(AiExecutionServiceImpl.class);

    private static final String SYSTEM_PROMPT = """
            Tu es un moteur d'analyse structuré de Trajectiv.
            Utilise uniquement les données fournies.
            Retourne exclusivement un objet JSON valide conforme au schéma imposé.
            N'ajoute ni Markdown, ni commentaire, ni texte avant ou après le JSON.
            """;

    private static final Pattern UNRESOLVED_VARIABLE =
            Pattern.compile("\\{\\{[A-Z0-9_]+}}", Pattern.MULTILINE);

    private final AiPromptRegistry promptRegistry;
    private final AiSchemaRegistry schemaRegistry;
    private final AiArtifactValidator artifactValidator;
    private final AiProvider aiProvider;
    private final ObjectMapper objectMapper;

    public AiExecutionServiceImpl(
            AiPromptRegistry promptRegistry,
            AiSchemaRegistry schemaRegistry,
            AiArtifactValidator artifactValidator,
            AiProvider aiProvider,
            ObjectMapper objectMapper
    ) {
        this.promptRegistry = promptRegistry;
        this.schemaRegistry = schemaRegistry;
        this.artifactValidator = artifactValidator;
        this.aiProvider = aiProvider;
        this.objectMapper = objectMapper;
    }

    @Override
    public AiExecutionResultBllDto execute(
            AiExecutionCommandBllDto command
    ) {
        VersionedPrompt prompt = promptRegistry.get(command.useCase());
        JsonNode schema = schemaRegistry.getSchema(command.useCase());
        String schemaVersion = schemaRegistry.getSchemaVersion(
                command.useCase()
        );
        String renderedPrompt = render(
                prompt.content(),
                command.variables()
        );

        AiProviderResponse providerResponse = aiProvider.generate(
                new AiProviderRequest(
                        command.useCase(),
                        SYSTEM_PROMPT,
                        renderedPrompt,
                        schemaVersion,
                        schema
                )
        );

        JsonNode artifact = parseArtifact(providerResponse);
        ArtifactValidationResult validation = artifactValidator.validate(
                schemaVersion,
                schema,
                artifact
        );

        if (!validation.valid()) {
            throw new AiArtifactValidationException(
                    schemaVersion,
                    validation.errors()
            );
        }

        return new AiExecutionResultBllDto(
                UUID.randomUUID(),
                command.useCase(),
                schemaVersion,
                prompt.version(),
                providerResponse.provider(),
                providerResponse.model(),
                providerResponse.durationMillis(),
                artifact
        );
    }

    private String render(
            String template,
            Map<String, String> variables
    ) {
        String rendered = template;
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            rendered = rendered.replace(
                    "{{" + entry.getKey() + "}}",
                    entry.getValue()
            );
        }

        Matcher unresolved = UNRESOLVED_VARIABLE.matcher(rendered);
        if (unresolved.find()) {
            throw new AiPromptRenderingException(
                    "Prompt variable was not provided: "
                            + unresolved.group()
            );
        }
        return rendered;
    }

    private JsonNode parseArtifact(
            AiProviderResponse providerResponse
    ) {
        String candidate = stripMarkdownFence(
                providerResponse.rawOutput()
        ).trim();

        if (!candidate.startsWith("{")) {
            int firstObject = candidate.indexOf('{');
            int lastObject = candidate.lastIndexOf('}');
            if (firstObject >= 0 && lastObject > firstObject) {
                candidate = candidate.substring(
                        firstObject,
                        lastObject + 1
                );
            }
        }

        try {
            JsonNode artifact = objectMapper.readTree(candidate);
            if (artifact == null || !artifact.isObject()) {
                LOGGER.warn(
                        "AI provider returned a non-object JSON response: provider={}",
                        providerResponse.provider()
                );
                throw AiProviderException.invalidResponse(
                        providerResponse.provider()
                );
            }
            return artifact;
        } catch (JsonProcessingException exception) {
            LOGGER.warn(
                    "AI provider returned invalid JSON: provider={}",
                    providerResponse.provider(),
                    exception
            );
            throw AiProviderException.invalidResponse(
                    providerResponse.provider(),
                    exception
            );
        }
    }

    private String stripMarkdownFence(String value) {
        String trimmed = value == null ? "" : value.trim();
        if (!trimmed.startsWith("```")) {
            return trimmed;
        }

        int firstLineBreak = trimmed.indexOf('\n');
        int closingFence = trimmed.lastIndexOf("```");
        if (firstLineBreak < 0 || closingFence <= firstLineBreak) {
            return trimmed;
        }

        return trimmed
                .substring(firstLineBreak + 1, closingFence)
                .trim();
    }
}