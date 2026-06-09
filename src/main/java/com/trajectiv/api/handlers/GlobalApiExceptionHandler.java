package com.trajectiv.api.handlers;

import com.trajectiv.api.dto.errors.ApiErrorResponse;
import com.trajectiv.bll.exceptions.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalApiExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorResponse> handleBusinessException(
            BusinessException exception,
            HttpServletRequest request
    ) {
        var status = exception.getErrorCode().httpStatus();

        ApiErrorResponse response = new ApiErrorResponse(
                exception.getErrorCode().name(),
                exception.getMessage(),
                status.value(),
                Instant.now(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(status)
                .body(response);
    }
}
