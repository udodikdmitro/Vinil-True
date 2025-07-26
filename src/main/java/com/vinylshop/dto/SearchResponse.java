package com.vinylshop.dto;

public record SearchResponse(
    PageDto<ProductDto> vinyls,
    PageDto<ProductDto> giftCertificates
) { }
