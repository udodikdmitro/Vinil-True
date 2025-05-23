package com.vinylshop.exception;

public class ResourceAlreadyExistException extends ResourceException {

    public ResourceAlreadyExistException() {
    }

    public ResourceAlreadyExistException(Object id, String resourceName) {
        super(id, resourceName);
    }

    public ResourceAlreadyExistException(String message, Object id, String resourceName) {
        super(message, id, resourceName);
    }

    public ResourceAlreadyExistException(String message, Throwable cause, Object id, String resourceName) {
        super(message, cause, id, resourceName);
    }

    public ResourceAlreadyExistException(Throwable cause, Object id, String resourceName) {
        super(cause, id, resourceName);
    }

    public ResourceAlreadyExistException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Object id, String resourceName) {
        super(message, cause, enableSuppression, writableStackTrace, id, resourceName);
    }

}
