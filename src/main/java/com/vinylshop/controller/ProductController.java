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

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final FileMetadataMapper fileMetadataMapper;

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
