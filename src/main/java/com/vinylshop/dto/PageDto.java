package com.vinylshop.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public record PageDto<T>(
        List<T> content,
        int size,
        int number,
        long totalElements,
        int totalPages
) {
    public PageDto(Page<T> page) {
        this(page.getContent(), page.getSize(), page.getNumber(), page.getTotalElements(), page.getTotalPages());
    }
}