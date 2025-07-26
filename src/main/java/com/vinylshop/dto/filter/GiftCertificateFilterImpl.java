package com.vinylshop.dto.filter;

import java.math.BigDecimal;

public record GiftCertificateFilterImpl(
    String search,
    BigDecimal priceFrom,
    BigDecimal priceTo,
    Boolean onSale,
    Boolean popularFirst
) implements GiftCertificateFilter {

    public static GiftCertificateFilter from(GiftCertificateFilter other) {
        return new GiftCertificateFilterImpl(other.search(), other.priceFrom(), other.priceTo(), other.onSale(), other.popularFirst());
    }

}
