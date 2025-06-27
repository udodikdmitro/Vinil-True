package com.vinylshop.dto;

public record ReviewCreateRequest(
    Long productId,
    Long userId,
    Integer rating,
    String comment
) { }
