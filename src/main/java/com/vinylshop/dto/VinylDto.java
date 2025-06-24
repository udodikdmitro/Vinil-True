package com.vinylshop.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vinylshop.entity.ReleaseType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VinylDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    private String title;
    private String album;
    private String artist;
    private Integer year;
    private String label;
    private String countryOfOrigin;
    private String catalogCode;
    private GenreDto genre;
    private String condition;
    private String envelopeCondition;
    private BigDecimal price;
    private Currency currency;
    private String note;
    private ReleaseType releaseType;
    private Integer quantity;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<String> imageUrls;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime updatedAt;

}

