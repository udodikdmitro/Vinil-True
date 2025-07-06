package com.vinylshop.dto;

public record ReviewCreateRequest(
    Long productId,
    Integer rating,
    String comment
) { }
