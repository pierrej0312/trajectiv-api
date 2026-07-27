package com.trajectiv.config.ai;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URI;
import java.time.Duration;

@ConfigurationProperties(prefix = "trajectiv.ai.ollama")
public record OllamaProperties(
        URI baseUrl,
        String model,
        Duration connectTimeout,
        Duration requestTimeout,
        Double temperature,
        Integer numCtx,
        Integer numPredict,
        Boolean think
) {

    private static final String DEFAULT_MODEL =
            "qwen3:8b-q4_K_M";

    public OllamaProperties {
        baseUrl = baseUrl == null
                ? URI.create("http://localhost:11434")
                : baseUrl;

        model = model == null || model.isBlank()
                ? DEFAULT_MODEL
                : model.trim();

        connectTimeout = connectTimeout == null
                ? Duration.ofSeconds(5)
                : connectTimeout;

        requestTimeout = requestTimeout == null
                ? Duration.ofMinutes(5)
                : requestTimeout;

        temperature = temperature == null
                ? 0.0d
                : temperature;

        numCtx = numCtx == null
                ? 16_384
                : numCtx;

        numPredict = numPredict == null
                ? 4_096
                : numPredict;

        think = think != null && think;

        if (temperature < 0.0d || temperature > 2.0d) {
            throw new IllegalArgumentException(
                    "temperature must be between 0 and 2"
            );
        }

        if (numCtx < 1_024) {
            throw new IllegalArgumentException(
                    "numCtx must be at least 1024"
            );
        }

        if (numPredict < 256) {
            throw new IllegalArgumentException(
                    "numPredict must be at least 256"
            );
        }
    }
}
