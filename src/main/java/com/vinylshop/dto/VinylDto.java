package com.vinylshop.dto;

import com.vinylshop.entity.ReleaseType;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VinylDto extends ProductDto {

    private String album;
    private String artist;
    private Integer year;
    private String label;
    private String countryOfOrigin;
    private String catalogCode;
    private GenreDto genre;
    private String condition;
    private String envelopeCondition;
    private String note;
    private ReleaseType releaseType;

}

