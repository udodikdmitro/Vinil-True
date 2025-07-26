package com.vinylshop.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ProductType {
    VINYL("vinyl"),
    GIFT_CERTIFICATE("gift-certificate");

    private final String value;

    ProductType(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return value;
    }

    @JsonCreator
    public static ProductType fromValue(String value) {
        for (ProductType type : ProductType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown type: " + value);
    }

}