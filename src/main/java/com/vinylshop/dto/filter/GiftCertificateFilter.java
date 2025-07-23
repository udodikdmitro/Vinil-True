package com.vinylshop.dto.filter;

import com.vinylshop.entity.ProductType;

public interface GiftCertificateFilter extends ProductFilter {

    default ProductType type() {
        return ProductType.GIFT_CERTIFICATE;
    }

}
