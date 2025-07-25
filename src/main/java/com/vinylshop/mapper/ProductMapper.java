package com.vinylshop.mapper;

import com.vinylshop.dto.ProductDto;
import com.vinylshop.entity.FileMetadata;
import com.vinylshop.entity.Product;
import org.mapstruct.*;

import java.util.List;

@Mapper
public interface ProductMapper {

    @Mappings({
        @Mapping(target = "imageUrls", ignore = true),
        @Mapping(target = "_links", ignore = true)
    })
    ProductDto toDto(Product entity);

    @Named("mapFileMetadataToUrl")
    default List<String> mapFileMetadataToUrl(Product entity) {
        return entity.getImages()
            .stream()
            .map(FileMetadata::getContentUrl)
            .toList();
    }

}
