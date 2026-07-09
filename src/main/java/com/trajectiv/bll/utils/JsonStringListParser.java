package com.trajectiv.bll.utils;

import com.fasterxml.jackson.core.type.TypeReference;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JsonStringListParser {

    private static final TypeReference<List<String>> STRING_LIST_TYPE =
            new TypeReference<>() {
            };

    private final ObjectMapper objectMapper;

    public JsonStringListParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<String> parse(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }

        try {
            return objectMapper.readValue(json, STRING_LIST_TYPE);
        } catch (Exception exception) {
            return List.of();
        }
    }
}