package com.vinylshop.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    private BigDecimal totalPrice;

    private String currency;

    private List<CartItemDto> items;

    private LocalDateTime updatedAt;

}
