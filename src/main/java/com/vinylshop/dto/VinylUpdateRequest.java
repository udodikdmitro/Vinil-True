package com.vinylshop.dto;

import com.vinylshop.entity.ReleaseType;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class VinylUpdateRequest extends ProductUpdateRequest {

    private String artist;
    private String album;

    @Positive(message = "must be a positive")
    private Long genreId;

    @Positive(message = "must be a positive")
    private Integer year;

    private String countryOfOrigin;
    private String catalogCode;
    private String label;
    private ReleaseType releaseType;
    private String condition;
    private String envelopCondition;
    private String note;

}
