package com.vinylshop.dto.filter;

import com.vinylshop.entity.ReleaseType;

import java.math.BigDecimal;
import java.util.List;

public record VinylFilter(
    Long genreId,
    String artist,
    String album,
    BigDecimal priceFrom,
    BigDecimal priceTo,
    Boolean onSale,
    Integer yearFrom,
    Integer yearTo,
    List<ReleaseType> releaseTypes
) implements ProductFilter { }
