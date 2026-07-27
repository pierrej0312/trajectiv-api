package com.trajectiv.config.ai;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.net.http.HttpClient;

@Configuration(proxyBeanMethods = false)
@Profile("local-ai")
@EnableConfigurationProperties(OllamaProperties.class)
public class AiConfiguration {

    @Bean
    @Qualifier("ollamaHttpClient")
    public HttpClient ollamaHttpClient(
            OllamaProperties properties
    ) {
        return HttpClient.newBuilder()
                .connectTimeout(
                        properties.connectTimeout()
                )
                .version(
                        HttpClient.Version.HTTP_1_1
                )
                .build();
    }
}