package com.vinylshop.dto.filter;

import com.vinylshop.entity.ReleaseType;
import org.springframework.context.annotation.Primary;

import java.math.BigDecimal;
import java.util.List;

public record VinylFilterImpl(
    String search,
    Long genreId,
    String artist,
    String album,
    BigDecimal priceFrom,
    BigDecimal priceTo,
    Boolean onSale,
    Integer yearFrom,
    Integer yearTo,
    List<ReleaseType> releaseTypes
) implements VinylFilter {

    public static VinylFilter from(VinylFilter other) {
        return new VinylFilterImpl(other.search(), other.genreId(), other.artist(), other.album(), other.priceFrom(), other.priceTo(), other.onSale(), other.yearFrom(), other.yearTo(), other.releaseTypes());
    }

}
