package com.vinylshop.dto.filter;

import com.vinylshop.entity.ReleaseType;

import java.math.BigDecimal;

public record VinylFilter(
    Long genreId,
    String artist,
    String album,
    BigDecimal priceFrom,
    BigDecimal priceTo,
    Boolean onSale,
    Integer yearFrom,
    Integer yearTo,
    ReleaseType releaseType
) implements ProductFilter { }
