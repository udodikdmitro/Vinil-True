package com.vinylshop.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;

@Data
public class ProductDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    private String title;

    private BigDecimal price;

    private BigDecimal discountPrice;

    private BigDecimal discountValue;

    private Currency currency;

    private Integer quantity;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<String> imageUrls;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime updatedAt;

}
