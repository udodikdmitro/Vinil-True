package com.vinylshop.exception;

public class ResourceNotFoundException extends ResourceException {

    public ResourceNotFoundException() {
    }

    public ResourceNotFoundException(Object id, String resourceName) {
        super(id, resourceName);
    }

    public ResourceNotFoundException(String message, Object id, String resourceName) {
        super(message, id, resourceName);
    }

    public ResourceNotFoundException(String message, Throwable cause, Object id, String resourceName) {
        super(message, cause, id, resourceName);
    }

    public ResourceNotFoundException(Throwable cause, Object id, String resourceName) {
        super(cause, id, resourceName);
    }

    public ResourceNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Object id, String resourceName) {
        super(message, cause, enableSuppression, writableStackTrace, id, resourceName);
    }
}
