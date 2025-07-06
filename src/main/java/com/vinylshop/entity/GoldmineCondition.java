package com.vinylshop.entity;

public enum GoldmineCondition {
    M("M", "Mint"),
    NM("NM", "Near Mint"),

    // nonstandard
    NM_MINUS("NM-", "Near Mint"),

    // nonstandard
    NM_PLUS("NM+", "Near Mint"),

    VG("VG", "Very Good"),
    VG_MINUS("VG-", "Very Good-"),
    VG_PLUS("VG+", "Very Good+"),
    G("G", "Good"),
    F("F", "Fair"),
    EX("EX", "Excellent"),
    EX_PLUS("EX+", "Excellent+"),
    EX_MINUS("EX-", "Excellent-"),
    P("P", "Poor"),
    SS("SS", "Still Sealed");

    private final String code;
    private final String description;

    GoldmineCondition(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String code() {
        return code;
    }

    public String description() {
        return description;
    }

}
