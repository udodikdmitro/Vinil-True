package com.vinylshop.dto;

public record ReviewUpdateRequest(
    Integer rating,
    String comment
) { }
