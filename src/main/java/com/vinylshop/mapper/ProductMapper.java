package com.vinylshop.mapper;

import com.vinylshop.dto.ProductDto;
import com.vinylshop.entity.FileMetadata;
import com.vinylshop.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import java.util.List;

@Mapper
public interface ProductMapper {

    @Mappings({
        @Mapping(target = "imageUrls", ignore = true)
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
