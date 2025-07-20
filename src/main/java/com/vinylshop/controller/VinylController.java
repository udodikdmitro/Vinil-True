package com.vinylshop.controller;

import com.vinylshop.dto.PageDto;
import com.vinylshop.dto.VinylDto;
import com.vinylshop.dto.VinylUpdateRequest;
import com.vinylshop.dto.filter.VinylFilter;
import com.vinylshop.entity.Vinyl;
import com.vinylshop.mapper.VinylMapper;
import com.vinylshop.service.VinylService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class VinylController {

    private final VinylService vinylService;
    private final VinylMapper vinylMapper;

    @GetMapping("/vinyls")
    public ResponseEntity<PageDto<VinylDto>> getAll(
        @ModelAttribute VinylFilter filter,
        @PageableDefault(page = 0, size = 10) Pageable pageable
    ) {
        Page<VinylDto> vinylPage = vinylService.findAll(filter, pageable);
        return ResponseEntity.ok(new PageDto<>(vinylPage));
    }

    @GetMapping("/vinyls/{id}")
    public ResponseEntity<VinylDto> getVinyl(
        @PathVariable("id") Long id,
        Locale locale
    ) {
        return ResponseEntity.of(vinylService.findById(id).map(x -> vinylMapper.toLocalizeDto(x, locale)));
    }

    @PostMapping(value = "/admin/vinyls", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<VinylDto> createVinyl(
            @RequestPart("vinyl") VinylDto dto,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            UriComponentsBuilder uriBuilder
    ) {
        VinylDto savedVinylDto = vinylService.saveFromDto(dto, images);
        URI uri = uriBuilder.path("/api/v1/vinyls/{id}").build(savedVinylDto.getId());
        return ResponseEntity.created(uri).body(savedVinylDto);
    }

    @PostMapping("/admin/vinyls/import")
    public ResponseEntity<?> importExcel(@RequestParam("file") MultipartFile file) {
        vinylService.importFromExcel(file);
        return ResponseEntity.ok("Імпортовано");
    }

    @PatchMapping("/admin/vinyls/{id}")
    public ResponseEntity<VinylDto> updateVinylById(
        @PathVariable Long id,
        @RequestBody @Valid VinylUpdateRequest request,
        Locale locale
    ) {
        Vinyl updated = vinylService.updateById(id, request);
        return ResponseEntity.ok(vinylMapper.toLocalizeDto(updated, locale));
    }

}

