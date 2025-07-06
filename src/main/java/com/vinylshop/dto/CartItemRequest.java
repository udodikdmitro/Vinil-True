package com.vinylshop.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemRequest {

    @NotNull(message = "can't be null")
    @Positive(message = "can't be zero or negative")
    private Long vinylId;

    @NotNull(message = "can't be null")
    @Positive(message = "can't be zero or negative")
    private Integer quantity;

}
