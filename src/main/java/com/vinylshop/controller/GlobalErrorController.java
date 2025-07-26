package com.vinylshop.controller;

import com.vinylshop.exception.ResourceAlreadyExistException;
import com.vinylshop.exception.ResourceException;
import com.vinylshop.exception.ResourceNotFoundException;
import org.springframework.core.convert.ConversionFailedException;
import jakarta.servlet.ServletException;
import jakarta.servlet.UnavailableException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.nio.file.NoSuchFileException;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalErrorController {

    private final MessageSource messageSource;

    @ExceptionHandler({
            NoSuchElementException.class,
            NoSuchFileException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ProblemDetail handleNotFound(Exception ex, Locale locale) {
        return buildProblemDetail(HttpStatus.NOT_FOUND, ex, locale);
    }

    @ExceptionHandler(ResourceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleResourceException(ResourceException ex, Locale locale) {
        HttpStatus status;
        String messageCode;
        switch (ex) {
            case ResourceNotFoundException e -> {
                status = HttpStatus.NOT_FOUND;
                messageCode = "error.resource.not-found";
            }
            case ResourceAlreadyExistException e -> {
                status = HttpStatus.CONFLICT;
                messageCode = "error.resource.already-exist";
            }
            default -> {
                status = HttpStatus.BAD_REQUEST;
                messageCode = "httpstatus.400";
            }
        };

        ProblemDetail problemDetail = buildProblemDetail(status, ex, locale, messageCode);
        problemDetail.setProperty("resource", ex.getResourceName());
        problemDetail.setProperty("resourceId", ex.getId());
        return problemDetail;
    }

    @ExceptionHandler({
            IllegalArgumentException.class,
            IllegalStateException.class,
            ConversionFailedException.class,
            MethodArgumentTypeMismatchException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleBadRequest(Exception ex, Locale locale) {
        return buildProblemDetail(HttpStatus.BAD_REQUEST, ex, locale);
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ProblemDetail handleBadCredentialsException(BadCredentialsException ex, Locale locale) {
        return buildProblemDetail(HttpStatus.UNAUTHORIZED, ex, locale,
            "error.auth.bad-credentials-exception");
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ProblemDetail handleAccessDeniedException(AccessDeniedException ex, Locale locale) {
        return buildProblemDetail(HttpStatus.FORBIDDEN, ex, locale,
            "error.auth.access-denied-exception");
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ProblemDetail handleAuthentication(AuthenticationException ex, Locale locale) {
        return buildProblemDetail(HttpStatus.UNAUTHORIZED, ex, locale,
            "error.auth.unauthorized");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleValidationErrors(MethodArgumentNotValidException ex, Locale locale) {
        ProblemDetail problemDetail = buildProblemDetail(HttpStatus.BAD_REQUEST, ex, locale,
            "error.validation.method-argument-not-valid-exception");
        appendErrorsProperty(problemDetail, ex.getBindingResult());
        return problemDetail;
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleBindException(BindException ex, Locale locale) {
        ProblemDetail problemDetail = buildProblemDetail(HttpStatus.BAD_REQUEST, ex, locale,
            "error.validation.bind-exception");
        appendErrorsProperty(problemDetail, ex.getBindingResult());
        return problemDetail;
    }

    private void appendErrorsProperty(ProblemDetail problemDetail, BindingResult result) {
        problemDetail.setProperty("errors", result
                .getFieldErrors()
                .stream()
                .collect(Collectors.groupingBy(
                        FieldError::getField,
                        Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList())
                ))
        );
    }

    @ExceptionHandler(ServletException.class)
    public ResponseEntity<ProblemDetail> handleServletException(ServletException ex, Locale locale) {
        ProblemDetail problemDetail;
        if (ex instanceof ErrorResponse errorResponse) {
            problemDetail = errorResponse.getBody();
        } else {
            HttpStatus status = switch (ex) {
                case HttpMediaTypeNotAcceptableException e -> HttpStatus.NOT_ACCEPTABLE;
                case HttpMediaTypeNotSupportedException e -> HttpStatus.UNSUPPORTED_MEDIA_TYPE;
                case HttpRequestMethodNotSupportedException e -> HttpStatus.METHOD_NOT_ALLOWED;
                case ServletRequestBindingException e -> HttpStatus.BAD_REQUEST;
                case NoHandlerFoundException e -> HttpStatus.NOT_FOUND;
                case NoResourceFoundException e -> HttpStatus.NOT_FOUND;
                case HttpSessionRequiredException e -> HttpStatus.UNAUTHORIZED;
                case UnavailableException e -> HttpStatus.SERVICE_UNAVAILABLE;
                default -> HttpStatus.INTERNAL_SERVER_ERROR;
            };
            problemDetail = buildProblemDetail(status, ex, locale);
        }
        return ResponseEntity.status(problemDetail.getStatus()).body(problemDetail);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ProblemDetail> internalServerError(Exception ex, Locale locale) {
        ProblemDetail problemDetail = buildProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR, ex, locale);
        return ResponseEntity.status(problemDetail.getStatus()).body(problemDetail);
    }

    private ProblemDetail buildProblemDetail(HttpStatus status, Exception ex, Locale locale) {
        return buildProblemDetail(status, ex, locale, getStatusMessageCode(status));
    }

    private ProblemDetail buildProblemDetail(HttpStatus status, Exception ex, Locale locale, String detailMessageCode) {
        String defaultTitle = status.getReasonPhrase();
        String defaultMessage = ex.getLocalizedMessage();

        String titleMessageCode = getStatusMessageCode(status);
        String title = messageSource.getMessage(titleMessageCode, new Object[0], defaultTitle, locale);

        String detail = detailMessageCode == null
            ? defaultMessage
            : messageSource.getMessage(detailMessageCode, new Object[0], defaultMessage, locale);

        return buildProblemDetail(status, title, detail);
    }

    private ProblemDetail buildProblemDetail(HttpStatus status, String title, String detail) {
        ProblemDetail problem = ProblemDetail.forStatus(status);
        problem.setTitle(title);
        problem.setDetail(detail);
        return problem;
    }

    private String getStatusMessageCode(HttpStatus status) {
        return "httpstatus." + status.value();
    }

}