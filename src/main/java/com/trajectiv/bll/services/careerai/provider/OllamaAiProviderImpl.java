package com.trajectiv.bll.services.careerai.provider;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.trajectiv.bll.exceptions.careerai.AiProviderException;
import com.trajectiv.bll.models.careerai.AiProviderRequest;
import com.trajectiv.bll.models.careerai.AiProviderResponse;
import com.trajectiv.config.ai.OllamaProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;

@Component
@Profile("local-ai")
public final class OllamaAiProviderImpl implements AiProvider {

    private static final String PROVIDER_NAME = "OLLAMA";

    private static final Logger LOGGER =
            LoggerFactory.getLogger(
                    OllamaAiProviderImpl.class
            );

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final OllamaProperties properties;

    public OllamaAiProviderImpl(
            @Qualifier("ollamaHttpClient")
            HttpClient httpClient,
            ObjectMapper objectMapper,
            OllamaProperties properties
    ) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
        this.properties = properties;
    }

    @Override
    public AiProviderResponse generate(
            AiProviderRequest request
    ) {
        long start = System.nanoTime();
        URI endpoint = properties
                .baseUrl()
                .resolve("/api/generate");

        try {
            ObjectNode body =
                    objectMapper.createObjectNode();

            body.put("model", properties.model());
            body.put("system", request.systemPrompt());
            body.put("prompt", request.userPrompt());
            body.put("stream", false);
            body.put("think", properties.think());
            body.set(
                    "format",
                    request.responseFormatSchema()
            );

            ObjectNode options =
                    body.putObject("options");

            options.put(
                    "temperature",
                    properties.temperature()
            );
            options.put(
                    "num_ctx",
                    properties.numCtx()
            );
            options.put(
                    "num_predict",
                    properties.numPredict()
            );

            HttpRequest httpRequest =
                    HttpRequest.newBuilder(endpoint)
                            .timeout(
                                    properties.requestTimeout()
                            )
                            .header(
                                    "Content-Type",
                                    "application/json"
                            )
                            .POST(
                                    HttpRequest.BodyPublishers
                                            .ofString(
                                                objectMapper
                                                    .writeValueAsString(
                                                        body
                                                    )
                                            )
                            )
                            .build();

            HttpResponse<String> response =
                    httpClient.send(
                            httpRequest,
                            HttpResponse.BodyHandlers
                                    .ofString()
                    );

            if (
                    response.statusCode() < 200
                            || response.statusCode() >= 300
            ) {
                LOGGER.warn(
                        "Ollama rejected the request: status={}, body={}",
                        response.statusCode(),
                        truncate(
                                response.body(),
                                1_000
                        )
                );

                throw AiProviderException
                        .invalidResponse(
                                PROVIDER_NAME
                        );
            }

            JsonNode responseBody =
                    objectMapper.readTree(
                            response.body()
                    );

            String output = responseBody
                    .path("response")
                    .asText(null);

            if (
                    output == null
                            || output.isBlank()
            ) {
                LOGGER.warn(
                        "Ollama returned no artifact: error={}",
                        truncate(
                                responseBody
                                        .path("error")
                                        .asText(
                                                "empty model response"
                                        ),
                                1_000
                        )
                );

                throw AiProviderException
                        .invalidResponse(
                                PROVIDER_NAME
                        );
            }

            long durationMillis =
                    (
                            System.nanoTime()
                                    - start
                    ) / 1_000_000;

            LOGGER.info(
                    "Local AI execution completed: useCase={}, model={}, durationMs={}",
                    request.useCase(),
                    properties.model(),
                    durationMillis
            );

            return new AiProviderResponse(
                    PROVIDER_NAME,
                    responseBody
                            .path("model")
                            .asText(
                                    properties.model()
                            ),
                    output,
                    durationMillis
            );
        } catch (HttpTimeoutException exception) {
            LOGGER.warn(
                    "Ollama request timed out: endpoint={}, timeout={}",
                    endpoint,
                    properties.requestTimeout(),
                    exception
            );

            throw AiProviderException.timeout(
                    PROVIDER_NAME,
                    exception
            );
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();

            LOGGER.warn(
                    "Ollama request was interrupted",
                    exception
            );

            throw AiProviderException.unavailable(
                    PROVIDER_NAME,
                    exception
            );
        } catch (IOException exception) {
            LOGGER.warn(
                    "Ollama request failed: endpoint={}",
                    endpoint,
                    exception
            );

            throw AiProviderException.unavailable(
                    PROVIDER_NAME,
                    exception
            );
        }
    }

    private static String truncate(
            String value,
            int maxLength
    ) {
        if (value == null) {
            return "";
        }

        return value.length() <= maxLength
                ? value
                : value.substring(
                        0,
                        maxLength
                ) + "…";
    }
}
