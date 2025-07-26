package com.vinylshop.dto;

import org.springframework.data.domain.Page;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public record PageDto<T>(
    List<T> content,
    int size,
    int number,
    long totalElements,
    int totalPages
) implements Iterable<T> {
    public PageDto(Page<T> page) {
        this(page.getContent(), page.getSize(), page.getNumber(), page.getTotalElements(), page.getTotalPages());
    }

    @Override
    public Iterator<T> iterator() {
        return Objects.requireNonNull(content).iterator();
    }

}