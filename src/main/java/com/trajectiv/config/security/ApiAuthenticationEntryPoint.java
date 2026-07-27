package com.trajectiv.config.security;

import com.trajectiv.api.handlers.ApiErrorResponseWriter;
import com.trajectiv.bll.exceptions.BusinessErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public final class ApiAuthenticationEntryPoint
        implements AuthenticationEntryPoint {

    private final BearerTokenAuthenticationEntryPoint delegate =
            new BearerTokenAuthenticationEntryPoint();

    private final ApiErrorResponseWriter errorResponseWriter;

    public ApiAuthenticationEntryPoint(
            ApiErrorResponseWriter errorResponseWriter
    ) {
        this.errorResponseWriter = errorResponseWriter;
    }

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authenticationException
    ) throws IOException, ServletException {
        delegate.commence(
                request,
                response,
                authenticationException
        );

        errorResponseWriter.write(
                request,
                response,
                BusinessErrorCode.AUTHENTICATION_REQUIRED,
                "Authentication is required to access this resource."
        );
    }
}