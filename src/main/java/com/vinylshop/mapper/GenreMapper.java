package com.vinylshop.mapper;

import com.vinylshop.dto.GenreDto;
import com.vinylshop.dto.GenreRequest;
import com.vinylshop.entity.Genre;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Locale;

@Mapper
public interface GenreMapper {

    @Mapping(target = "id", ignore = true)
    Genre toEntity(GenreRequest request);

    default GenreDto toDto(Genre entity, Locale locale) {
        if (entity == null) {
            return null;
        }
        String localizeGenre = switch (locale.getLanguage()) {
            case "uk" -> entity.getNameUk();
            default -> entity.getNameEn();
        };
        return new GenreDto(entity.getId(), localizeGenre);
    }


}
