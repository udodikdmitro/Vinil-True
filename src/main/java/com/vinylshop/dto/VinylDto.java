package com.vinylshop.dto;

import com.vinylshop.entity.ReleaseType;
import jakarta.persistence.Column;
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
    private String format;
    private String color;
    private Double vinylSize;
    private String limitedEditionNumber;
    private Boolean isEmbossing;
    private Integer totalPressing;
    private Double speed;
    private Integer discCount;
    private Long externalAlbumId;

}

