package com.vinylshop.dto.filter;

import java.math.BigDecimal;

public interface ProductFilter {

    BigDecimal priceFrom();
    BigDecimal priceTo();

}
