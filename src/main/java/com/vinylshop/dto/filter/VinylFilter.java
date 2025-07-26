package com.vinylshop.dto.filter;

import com.vinylshop.entity.ProductType;
import com.vinylshop.entity.ReleaseType;

import java.math.BigDecimal;
import java.util.List;

public interface VinylFilter extends ProductFilter {

    Long genreId();
    String artist();
    String album();
    BigDecimal priceFrom();
    BigDecimal priceTo();
    Boolean onSale();
    Integer yearFrom();
    Integer yearTo();
    List<ReleaseType> releaseTypes();

    default ProductType type() {
        return ProductType.VINYL;
    }

}
