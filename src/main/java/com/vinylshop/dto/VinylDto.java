package com.vinylshop.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VinylDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<String> imageUrls;

    private String title;
    private String artist;
    private Integer year;
    private BigDecimal price;
    private String currency;
    private BigDecimal originalPrice;
    private String originalCurrency;
    private Integer quantity;

}

