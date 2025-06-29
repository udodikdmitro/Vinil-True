package com.vinylshop.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReviewDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    private ProductDto product;

    private UserDto user;

    private Integer rating;

    private String comment;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime updatedAt;

}
