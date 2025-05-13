package com.vinylshop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileMetadataDto {

    private Long id;

    private String name;

    private String originalName;

    private String description;

    private String contentType;

    private String url;

    private String contentUrl;

    private long size;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}

