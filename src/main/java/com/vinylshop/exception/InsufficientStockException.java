package com.vinylshop.exception;

import lombok.Data;
import lombok.Getter;

@Getter
public class InsufficientStockException extends ResourceException {

    private final int quantity;

    public InsufficientStockException(Object id, String resourceName, int quantity) {
        super(id, resourceName);
        this.quantity = quantity;
    }

    public InsufficientStockException(String message, Object id, String resourceName, int quantity) {
        super(message, id, resourceName);
        this.quantity = quantity;
    }

    public InsufficientStockException(String message, Throwable cause, Object id, String resourceName, int quantity) {
        super(message, cause, id, resourceName);
        this.quantity = quantity;
    }

    public InsufficientStockException(Throwable cause, Object id, String resourceName, int quantity) {
        super(cause, id, resourceName);
        this.quantity = quantity;
    }

    public InsufficientStockException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Object id, String resourceName, int quantity) {
        super(message, cause, enableSuppression, writableStackTrace, id, resourceName);
        this.quantity = quantity;
    }

}
