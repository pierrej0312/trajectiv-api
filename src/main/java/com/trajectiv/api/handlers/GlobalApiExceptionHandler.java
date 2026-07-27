package com.trajectiv.api.handlers;

import com.trajectiv.api.dto.errors.ApiErrorResponse;
import com.trajectiv.api.dto.errors.ApiFieldError;
import com.trajectiv.bll.exceptions.BusinessErrorCode;
import com.trajectiv.bll.exceptions.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.method.ParameterErrors;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RestControllerAdvice
public class GlobalApiExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(GlobalApiExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorResponse> handleBusinessException(
            BusinessException exception,
            HttpServletRequest request
    ) {
        BusinessErrorCode errorCode = exception.getErrorCode();

        if (errorCode.httpStatus().is5xxServerError()) {
            LOGGER.error(
                    "Business exception: code={}, method={}, path={}",
                    errorCode,
                    request.getMethod(),
                    request.getRequestURI(),
                    exception
            );
        } else {
            LOGGER.debug(
                    "Business exception: code={}, method={}, path={}, message={}",
                    errorCode,
                    request.getMethod(),
                    request.getRequestURI(),
                    exception.getMessage()
            );
        }

        ApiErrorResponse response = createResponse(
                errorCode,
                exception.getPublicMessage(),
                request.getRequestURI(),
                List.of()
        );

        return ResponseEntity
                .status(errorCode.httpStatus())
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpectedException(
            Exception exception,
            HttpServletRequest request
    ) {
        LOGGER.error(
                "Unhandled exception: method={}, path={}",
                request.getMethod(),
                request.getRequestURI(),
                exception
        );

        BusinessErrorCode errorCode = BusinessErrorCode.INTERNAL_ERROR;
        ApiErrorResponse response = createResponse(
                errorCode,
                "An unexpected internal error occurred.",
                request.getRequestURI(),
                List.of()
        );

        return ResponseEntity
                .status(errorCode.httpStatus())
                .body(response);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        List<ApiFieldError> violations = new ArrayList<>();

        exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::toApiFieldError)
                .forEach(violations::add);

        exception.getBindingResult()
                .getGlobalErrors()
                .stream()
                .map(error -> new ApiFieldError(
                        error.getObjectName(),
                        resolveMessage(error)
                ))
                .forEach(violations::add);

        return validationResponse(headers, request, violations);
    }

    @Override
    protected ResponseEntity<Object> handleHandlerMethodValidationException(
            HandlerMethodValidationException exception,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        List<ApiFieldError> violations = new ArrayList<>();

        for (ParameterValidationResult result :
                exception.getParameterValidationResults()) {
            String parameterName = resolveParameterName(
                    result.getMethodParameter()
            );

            if (result instanceof ParameterErrors parameterErrors) {
                parameterErrors.getFieldErrors()
                        .stream()
                        .map(error -> new ApiFieldError(
                                parameterName + "." + error.getField(),
                                resolveMessage(error)
                        ))
                        .forEach(violations::add);

                parameterErrors.getGlobalErrors()
                        .stream()
                        .map(error -> new ApiFieldError(
                                parameterName,
                                resolveMessage(error)
                        ))
                        .forEach(violations::add);
            } else {
                result.getResolvableErrors()
                        .stream()
                        .map(error -> new ApiFieldError(
                                parameterName,
                                resolveMessage(error)
                        ))
                        .forEach(violations::add);
            }
        }

        exception.getCrossParameterValidationResults()
                .stream()
                .map(error -> new ApiFieldError(
                        "request",
                        resolveMessage(error)
                ))
                .forEach(violations::add);

        return validationResponse(headers, request, violations);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException exception,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        return errorResponse(
                headers,
                BusinessErrorCode.MALFORMED_REQUEST_BODY,
                "The request body is missing or malformed.",
                request,
                List.of()
        );
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestPart(
            MissingServletRequestPartException exception,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        return errorResponse(
                headers,
                BusinessErrorCode.MISSING_REQUEST_PART,
                "A required multipart request part is missing.",
                request,
                List.of(new ApiFieldError(
                        exception.getRequestPartName(),
                        "This multipart part is required."
                ))
        );
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException exception,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        return errorResponse(
                headers,
                BusinessErrorCode.VALIDATION_FAILED,
                "The request contains invalid parameters.",
                request,
                List.of(new ApiFieldError(
                        exception.getParameterName(),
                        "This request parameter is required."
                ))
        );
    }

    @Override
    protected ResponseEntity<Object> handleMaxUploadSizeExceededException(
            MaxUploadSizeExceededException exception,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        return errorResponse(
                headers,
                BusinessErrorCode.FILE_TOO_LARGE,
                "The uploaded file exceeds the maximum allowed size.",
                request,
                List.of()
        );
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException exception,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        return errorResponse(
                headers,
                BusinessErrorCode.UNSUPPORTED_MEDIA_TYPE,
                "The request content type is not supported.",
                request,
                List.of()
        );
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException exception,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        return errorResponse(
                headers,
                BusinessErrorCode.METHOD_NOT_ALLOWED,
                "The HTTP method is not supported for this resource.",
                request,
                List.of()
        );
    }

    @Override
    protected ResponseEntity<Object> handleNoResourceFoundException(
            NoResourceFoundException exception,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        return errorResponse(
                headers,
                BusinessErrorCode.ROUTE_NOT_FOUND,
                "The requested resource was not found.",
                request,
                List.of()
        );
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(
            TypeMismatchException exception,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        String field = exception.getPropertyName() == null
                ? "request"
                : exception.getPropertyName();

        return errorResponse(
                headers,
                BusinessErrorCode.VALIDATION_FAILED,
                "The request contains an invalid value.",
                request,
                List.of(new ApiFieldError(
                        field,
                        "The value has an invalid type or format."
                ))
        );
    }

    private ResponseEntity<Object> validationResponse(
            HttpHeaders headers,
            WebRequest request,
            List<ApiFieldError> violations
    ) {
        List<ApiFieldError> normalizedViolations = violations.stream()
                .distinct()
                .sorted(Comparator.comparing(ApiFieldError::field))
                .toList();

        return errorResponse(
                headers,
                BusinessErrorCode.VALIDATION_FAILED,
                "The request contains validation errors.",
                request,
                normalizedViolations
        );
    }

    private ResponseEntity<Object> errorResponse(
            HttpHeaders headers,
            BusinessErrorCode errorCode,
            String message,
            WebRequest request,
            List<ApiFieldError> violations
    ) {
        ApiErrorResponse body = createResponse(
                errorCode,
                message,
                resolvePath(request),
                violations
        );

        return new ResponseEntity<>(
                body,
                headers,
                errorCode.httpStatus()
        );
    }

    private ApiErrorResponse createResponse(
            BusinessErrorCode errorCode,
            String message,
            String path,
            List<ApiFieldError> violations
    ) {
        return new ApiErrorResponse(
                errorCode.name(),
                message,
                errorCode.httpStatus().value(),
                Instant.now(),
                path,
                violations
        );
    }

    private ApiFieldError toApiFieldError(FieldError error) {
        return new ApiFieldError(
                error.getField(),
                resolveMessage(error)
        );
    }

    private String resolveMessage(MessageSourceResolvable error) {
        String defaultMessage = error.getDefaultMessage();
        if (defaultMessage != null && !defaultMessage.isBlank()) {
            return defaultMessage;
        }

        String[] codes = error.getCodes();
        return codes == null || codes.length == 0
                ? "Invalid value."
                : codes[0];
    }

    private String resolveParameterName(MethodParameter parameter) {
        String parameterName = parameter.getParameterName();
        return parameterName == null || parameterName.isBlank()
                ? "argument[" + parameter.getParameterIndex() + "]"
                : parameterName;
    }

    private String resolvePath(WebRequest request) {
        if (request instanceof ServletWebRequest servletWebRequest) {
            return servletWebRequest
                    .getRequest()
                    .getRequestURI();
        }
        return "";
    }
}