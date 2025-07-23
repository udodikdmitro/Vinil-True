package com.vinylshop.controller;

import com.vinylshop.dto.FileMetadataDto;
import com.vinylshop.entity.Product;
import com.vinylshop.mapper.FileMetadataMapper;
import com.vinylshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import com.vinylshop.dto.PageDto;
import com.vinylshop.dto.ProductDto;
import com.vinylshop.dto.SearchResponse;
import com.vinylshop.dto.filter.GiftCertificateFilterImpl;
import com.vinylshop.dto.filter.GlobalProductFilter;
import com.vinylshop.dto.filter.VinylFilterImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final FileMetadataMapper fileMetadataMapper;

    @GetMapping("/products")
    public ResponseEntity<SearchResponse> search(
        @ModelAttribute GlobalProductFilter filter,
        @PageableDefault(page = 0, size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(productService.search(filter, pageable));
    }

    @GetMapping("/products/vinyls")
    public ResponseEntity<PageDto<ProductDto>> searchVinyls(
        @ModelAttribute VinylFilterImpl filter,
        @PageableDefault(page = 0, size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(productService.searchProducts(filter, pageable));
    }

    @GetMapping("/products/gift-certificates")
    public ResponseEntity<PageDto<ProductDto>> searchGiftCertificates(
        @ModelAttribute GiftCertificateFilterImpl filter,
        @PageableDefault(page = 0, size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(productService.searchProducts(filter, pageable));
    }

    @GetMapping("/products/{id}/images")
    public ResponseEntity<List<FileMetadataDto>> getAllImages(@PathVariable Long id) {
        Product product = productService.getByIdOrThrow(id);
        return ResponseEntity.ok(fileMetadataMapper
            .toDtoAll(product.getImages())
            .toList());
    }

    @PostMapping(value = "/admin/products/{id}/images", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<FileMetadataDto>> addImages(
        @PathVariable Long id,
        @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) {
        if (images == null || images.isEmpty()) {
            return getAllImages(id);
        }
        return ResponseEntity.ok(productService.addImagesFromMultipartFiles(id, images));
    }

    @DeleteMapping(value = "/admin/products/{id}/images")
    public ResponseEntity<?> removeImages(
        @PathVariable Long id,
        @RequestBody List<Long> imageIds
    ) {
        if (imageIds != null && !imageIds.isEmpty()) {
            productService.removeImages(id, imageIds);
        }
        return ResponseEntity.ok("Видалено");
    }

}
