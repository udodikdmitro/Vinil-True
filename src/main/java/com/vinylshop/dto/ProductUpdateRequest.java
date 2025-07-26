package com.vinylshop.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.math.BigDecimal;

import static com.vinylshop.util.Constants.BIG_DECIMAL_EMPTY_VALUE;

@Data
public abstract class ProductUpdateRequest {

    private String title;

    @Positive(message = "must be a positive")
    private BigDecimal price;

    @PositiveOrZero(message = "must be zero or a positive")
    private BigDecimal discountPrice = BIG_DECIMAL_EMPTY_VALUE;

    @Range(min = 0, max = 100, message = "must be between 0 and 100 inclusive")
    private BigDecimal discountValue;

    @PositiveOrZero(message = "must be zero or a positive")
    private Integer quantity;

}
