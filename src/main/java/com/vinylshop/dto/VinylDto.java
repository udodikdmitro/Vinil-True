package com.vinylshop.dto;

import lombok.*;

import java.util.List;

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
    private List<String> imageUrls;
}

