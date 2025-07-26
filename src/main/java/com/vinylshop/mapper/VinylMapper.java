package com.vinylshop.mapper;

import com.vinylshop.dto.GenreDto;
import com.vinylshop.dto.VinylDto;
import com.vinylshop.dto.VinylUpdateRequest;
import com.vinylshop.entity.Vinyl;
import org.mapstruct.*;

import java.util.Locale;
import java.util.stream.Stream;

@Mapper(uses = ProductMapper.class)
public interface VinylMapper {

    @Mappings({
        @Mapping(target = "images", ignore = true),
        @Mapping(target = "genre", ignore = true),
        @Mapping(target = "discountPrice", ignore = true),
        @Mapping(target = "discountValue", ignore = true)
    })
    Vinyl toEntity(VinylDto dto);

    @Mappings({
        @Mapping(target = "imageUrls", source = "entity", qualifiedByName = "mapFileMetadataToUrl"),
        @Mapping(target = "type", source = "dtype")
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

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mappings({
        @Mapping(target = "price", ignore = true),
        @Mapping(target = "discountPrice", ignore = true),
        @Mapping(target = "discountValue", ignore = true)
    })
    void updateNonNullFields(VinylUpdateRequest source, @MappingTarget Vinyl target);

}
