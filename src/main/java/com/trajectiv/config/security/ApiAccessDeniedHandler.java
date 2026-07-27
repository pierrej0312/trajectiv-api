package com.trajectiv.config.security;

import com.trajectiv.api.handlers.ApiErrorResponseWriter;
import com.trajectiv.bll.exceptions.BusinessErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public final class ApiAccessDeniedHandler
        implements AccessDeniedHandler {

    private final BearerTokenAccessDeniedHandler delegate =
            new BearerTokenAccessDeniedHandler();

    private final ApiErrorResponseWriter errorResponseWriter;

    public ApiAccessDeniedHandler(
            ApiErrorResponseWriter errorResponseWriter
    ) {
        this.errorResponseWriter = errorResponseWriter;
    }

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException, ServletException {
        delegate.handle(
                request,
                response,
                accessDeniedException
        );

        errorResponseWriter.write(
                request,
                response,
                BusinessErrorCode.ACCESS_DENIED,
                "You are not allowed to access this resource."
        );
    }
}