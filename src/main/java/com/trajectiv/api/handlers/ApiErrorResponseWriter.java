package com.trajectiv.api.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trajectiv.api.dto.errors.ApiErrorResponse;
import com.trajectiv.bll.exceptions.BusinessErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

@Component
public final class ApiErrorResponseWriter {

    private final ObjectMapper objectMapper;

    public ApiErrorResponseWriter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void write(
            HttpServletRequest request,
            HttpServletResponse response,
            BusinessErrorCode errorCode,
            String message
    ) throws IOException {
        response.setStatus(errorCode.httpStatus().value());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ApiErrorResponse body = new ApiErrorResponse(
                errorCode.name(),
                message,
                errorCode.httpStatus().value(),
                Instant.now(),
                request.getRequestURI()
        );

        objectMapper.writeValue(response.getOutputStream(), body);
        response.getOutputStream().flush();
    }
}