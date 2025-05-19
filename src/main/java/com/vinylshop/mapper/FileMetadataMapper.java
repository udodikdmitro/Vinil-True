package com.vinylshop.mapper;

import com.vinylshop.dto.FileMetadataDto;
import com.vinylshop.entity.FileMetadata;
import com.vinylshop.upload.UploadedFileAdapter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.stream.Stream;

@Mapper
public interface FileMetadataMapper {

    @Mapping(target = "fileData", ignore = true)
    FileMetadata toEntity(FileMetadataDto dto);

    @Mappings({
            @Mapping(target = "originalName", source = "originalFilename"),
            @Mapping(target = "fileData", ignore = true),
            @Mapping(target = "url", ignore = true),
            @Mapping(target = "contentUrl", ignore = true)
    })
    FileMetadata toEntity(UploadedFileAdapter uploadedFileAdapter);

    FileMetadataDto toDto(FileMetadata entity);

    Stream<FileMetadataDto> toDtoAll(Iterable<FileMetadata> entities);

}
