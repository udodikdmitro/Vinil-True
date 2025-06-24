package com.vinylshop.mapper;

import com.vinylshop.dto.GenreDto;
import com.vinylshop.dto.VinylDto;
import com.vinylshop.entity.FileMetadata;
import com.vinylshop.entity.Genre;
import com.vinylshop.entity.Vinyl;
import jdk.jfr.Name;
import org.mapstruct.*;
import org.springframework.cglib.core.Local;

import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

@Mapper
public interface VinylMapper {

    @Mappings({
        @Mapping(target = "images", ignore = true),
        @Mapping(target = "genre", ignore = true)
    })
    Vinyl toEntity(VinylDto dto);

    @Mappings({
        @Mapping(target = "imageUrls", source = "entity", qualifiedByName = "mapFileMetadataToUrl")
    })
    VinylDto toDto(Vinyl entity);

    Stream<VinylDto> toDtoAll(Iterable<Vinyl> entities);

    default VinylDto toLocalizeDto(Vinyl entity, Locale locale) {
        VinylDto dto = toDto(entity);
        if (dto == null) {
            return null;
        }
        String lang = locale == null || locale.getLanguage() == null ? "en" : locale.getLanguage();
        if (dto.getGenre() != null) {
            String localizeGenre = switch (lang) {
                case "uk" -> entity.getGenre().getNameUk();
                default -> entity.getGenre().getNameEn();
            };
            dto.setGenre(new GenreDto(entity.getGenre().getId(), localizeGenre));
        }
        return dto;
    }

    @Named("mapFileMetadataToUrl")
    default List<String> mapFileMetadataToUrl(Vinyl entity) {
        return entity.getImages()
                .stream()
                .map(FileMetadata::getContentUrl)
                .toList();
    }

}
