package com.vinylshop.controller;

import com.vinylshop.exception.ResourceAlreadyExistException;
import com.vinylshop.exception.ResourceException;
import com.vinylshop.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.NoSuchFileException;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalErrorController {

    @ExceptionHandler({
            ResourceNotFoundException.class,
            NoSuchElementException.class,
            NoSuchFileException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ProblemDetail handleNotFound(Exception ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problemDetail.setTitle("Not Found");
        problemDetail.setDetail(ex.getMessage());

        if (ex instanceof ResourceException resourceEx) {
            problemDetail.setProperty("resource", resourceEx.getResourceName());
        }

        return problemDetail;
    }

    @ExceptionHandler(ResourceAlreadyExistException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleResourceAlreadyExistException(ResourceAlreadyExistException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Already Exists");
        problemDetail.setDetail(ex.getMessage());
        problemDetail.setProperty("resource", ex.getResourceName());
        return problemDetail;
    }

    @ExceptionHandler(ResourceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleResourceException(ResourceException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Resource Error");
        problemDetail.setDetail(ex.getMessage());
        problemDetail.setProperty("resource", ex.getResourceName());
        return problemDetail;
    }

    @ExceptionHandler({
            IllegalArgumentException.class,
            IllegalStateException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleBadRequest(Exception ex) {
        return buildProblemDetail(HttpStatus.BAD_REQUEST, "Bad request", ex.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ProblemDetail handleBadCredentialsException(BadCredentialsException ex) {
        return buildProblemDetail(HttpStatus.UNAUTHORIZED, "Invalid login or password", ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ProblemDetail handleAccessDeniedException(AccessDeniedException ex) {
        return buildProblemDetail(HttpStatus.FORBIDDEN, "Access denied", ex.getMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ProblemDetail handleAuthentication(AuthenticationException ex) {
        return buildProblemDetail(HttpStatus.UNAUTHORIZED, "Unauthorized", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleValidationErrors(MethodArgumentNotValidException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Validation failed");
        problemDetail.setDetail("One or more fields have validation errors");
        appendErrorsProperty(problemDetail, ex.getBindingResult());
        return problemDetail;
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleBindException(BindException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Binding failed");
        problemDetail.setDetail("Invalid request data");
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

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ProblemDetail internalServerError(Throwable throwable) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
    }

    private ProblemDetail buildProblemDetail(HttpStatus status, String title, String detail) {
        ProblemDetail problem = ProblemDetail.forStatus(status);
        problem.setTitle(title);
        problem.setDetail(detail);
        return problem;
    }
}