package com.vinylshop.mapper;

import com.vinylshop.dto.ReviewCreateRequest;
import com.vinylshop.dto.ReviewDto;
import com.vinylshop.dto.ReviewUpdateRequest;
import com.vinylshop.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(uses = { VinylMapper.class, UserMapper.class })
public interface ReviewMapper {

    ReviewDto toDto(Review entity);

    @Mappings({
        @Mapping(target = "product", ignore = true),
        @Mapping(target = "user", ignore = true)
    })
    Review toEntity(ReviewDto dto);

    @Mappings({
        @Mapping(target = "product", ignore = true)
    })
    Review toEntity(ReviewCreateRequest dto);

    Review toEntity(ReviewUpdateRequest dto);

}
