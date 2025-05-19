package com.vinylshop.mapper;

import com.vinylshop.dto.VinylDto;
import com.vinylshop.entity.FileMetadata;
import com.vinylshop.entity.Vinyl;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Stream;

@Mapper
public interface VinylMapper {

    @Mappings({
            @Mapping(target = "images", ignore = true)
    })
    Vinyl toEntity(VinylDto dto);

    @Mapping(target = "imageUrls", source = "entity", qualifiedByName = "mapFileMetadataToUrl")
    VinylDto toDto(Vinyl entity);

    Stream<VinylDto> toDtoAll(Iterable<Vinyl> entities);

    @Named("mapFileMetadataToUrl")
    default List<String> mapFileMetadataToUrl(Vinyl entity) {
        return entity.getImages()
                .stream()
                .map(FileMetadata::getContentUrl)
                .toList();
    }

}
