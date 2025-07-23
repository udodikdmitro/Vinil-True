package com.vinylshop.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    private String title;

    private String subtitle;

    private BigDecimal price;

    private BigDecimal discountPrice;

    private BigDecimal discountValue;

    private Currency currency;

    private Integer quantity;

    private Double weight;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<String> imageUrls = new ArrayList<>();

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime updatedAt;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String type;

    public ProductDto(Long id, String title, String subtitle, BigDecimal price, Currency currency, Integer quantity, String mainImageUrl, LocalDateTime createdAt, LocalDateTime updatedAt, String type) {
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.price = price;
        this.currency = currency;
        this.quantity = quantity;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.type = type;
        this.imageUrls.add(mainImageUrl);
    }

}
