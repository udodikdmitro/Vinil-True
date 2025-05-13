package com.vinylshop.controller;

import com.vinylshop.dto.FileMetadataDto;
import com.vinylshop.dto.VinylDto;
import com.vinylshop.entity.Vinyl;
import com.vinylshop.service.FileService;
import com.vinylshop.service.VinylService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/vinyls")
@RequiredArgsConstructor
public class VinylController {

    private final VinylService vinylService;
    private final FileService fileService;

    @GetMapping
    public List<VinylDto> getAll() {
        return vinylService.findTop10ForMainPage();
    }

    @GetMapping("/{id}")
    public ResponseEntity<VinylDto> getVinyl(@PathVariable("id") Long id) {
        return ResponseEntity.of(vinylService.findById(id).map(vinylService::toDto));
    }

    @GetMapping("/{id}/images")
    public ResponseEntity<List<FileMetadataDto>> getAllImages(@PathVariable Long id) {
        Optional<Vinyl> metadata = vinylService.findById(id);
        return ResponseEntity.of(metadata.map(x -> x.getImages()
                .stream()
                .map(fileService::toDto)
                .toList()));
    }

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<VinylDto> createVinyl(
            @RequestPart("vinyl") VinylDto dto,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            UriComponentsBuilder uriBuilder
    ) {
        VinylDto savedVinylDto = vinylService.saveFromDto(dto, images);
        URI uri = uriBuilder.path("/api/vinyls/{id}").build(savedVinylDto.getId());
        return ResponseEntity.created(uri).body(savedVinylDto);
    }

    @PostMapping(value = "/{id}/images/add", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<FileMetadataDto>> addImages(
            @PathVariable Long id,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) {
        if (images == null || images.isEmpty()) {
            return getAllImages(id);
        }
        return ResponseEntity.ok(vinylService.addImagesFromMultipartFiles(id, images));
    }

    @PostMapping(value = "/{id}/images/delete")
    public ResponseEntity<?> removeImages(
            @PathVariable Long id,
            @RequestBody List<Long> imageIds
    ) {
        if (imageIds != null && !imageIds.isEmpty()) {
            vinylService.removeImages(id, imageIds);
        }
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/import")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> importExcel(@RequestParam("file") MultipartFile file) {
        vinylService.importFromExcel(file);
        return ResponseEntity.ok("Імпортовано");
    }
}

