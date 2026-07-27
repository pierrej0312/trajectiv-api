package com.trajectiv.bll.dto.careerai;

import com.trajectiv.bll.models.careerai.AiUseCase;

import java.util.Map;
import java.util.Objects;

public record AiExecutionCommandBllDto(
        AiUseCase useCase,
        Map<String, String> variables
) {
    public AiExecutionCommandBllDto {
        Objects.requireNonNull(useCase, "useCase is required");
        variables = Map.copyOf(Objects.requireNonNull(variables, "variables are required"));
    }
}