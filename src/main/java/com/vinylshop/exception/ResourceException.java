package com.vinylshop.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResourceException extends RuntimeException {

    private Object id;
    private String resourceName;

    public ResourceException() {
    }

    public ResourceException(Object id, String resourceName) {
        this.id = id;
        this.resourceName = resourceName;
    }

    public ResourceException(String message, Object id, String resourceName) {
        super(message);
        this.id = id;
        this.resourceName = resourceName;
    }

    public ResourceException(String message, Throwable cause, Object id, String resourceName) {
        super(message, cause);
        this.id = id;
        this.resourceName = resourceName;
    }

    public ResourceException(Throwable cause, Object id, String resourceName) {
        super(cause);
        this.id = id;
        this.resourceName = resourceName;
    }

    public ResourceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Object id, String resourceName) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.id = id;
        this.resourceName = resourceName;
    }
}
