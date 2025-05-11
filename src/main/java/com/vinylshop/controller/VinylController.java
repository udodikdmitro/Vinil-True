package com.vinylshop.controller;

import com.vinylshop.dto.VinylDto;
import com.vinylshop.entity.Vinyl;
import com.vinylshop.service.VinylService;
import com.vinylshop.upload.MultipartFileUploadedFileAdapter;
import com.vinylshop.upload.UploadedFileAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/vinyls")
@RequiredArgsConstructor
public class VinylController {

    private final VinylService vinylService;

    @GetMapping
    public List<VinylDto> getAll() {
        return vinylService.findTop10ForMainPage();
    }

    @GetMapping("/{id}")
    public ResponseEntity<VinylDto> getVinyl(@PathVariable("id") Long id) {
        return ResponseEntity.of(vinylService.findById(id));
    }

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<VinylDto> createVinyl(
            @RequestPart("vinyl") VinylDto dto,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            UriComponentsBuilder uriBuilder
    ) {
        if (images == null) {
            images = Collections.emptyList();
        }

        List<UploadedFileAdapter> uploadedFileAdapters = images.stream()
                .map(image -> (UploadedFileAdapter) new MultipartFileUploadedFileAdapter(image))
                .toList();

        Vinyl vinyl = new Vinyl();
        vinyl.setTitle(dto.getTitle());
        vinyl.setArtist(dto.getArtist());
        vinyl.setYear(dto.getYear());

        vinyl = vinylService.create(vinyl, uploadedFileAdapters);

        dto.setId(vinyl.getId());
        URI uri = uriBuilder.path("/api/vinyls/{id}").build(vinyl.getId());
        return ResponseEntity.created(uri)
                .body(vinylService.toDto(vinyl));
    }

    @PostMapping("/import")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> importExcel(@RequestParam("file") MultipartFile file) {
        vinylService.importFromExcel(file);
        return ResponseEntity.ok("Імпортовано");
    }
}

