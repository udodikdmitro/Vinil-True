package com.vinylshop.dto;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VinylDto {
    private Long id;
    private String title;
    private String artist;
    private Integer year;
    private byte[] image;
}

