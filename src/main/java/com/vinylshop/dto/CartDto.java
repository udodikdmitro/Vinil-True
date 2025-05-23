package com.vinylshop.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vinylshop.entity.CartItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    private BigDecimal totalPrice;

    private String currency;

    private PageDto<CartItemDto> items;

    private LocalDateTime updatedAt;

}
