package com.vinylshop.dto.filter;

import com.vinylshop.entity.ProductType;

import java.math.BigDecimal;

public interface ProductFilter {

    String search();
    BigDecimal priceFrom();
    BigDecimal priceTo();
    Boolean onSale();
    ProductType type();
    Boolean popularFirst();

}
