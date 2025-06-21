package com.vinylshop.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    private ProductDto product;

    private Integer quantity;

    private BigDecimal totalPrice;

}
