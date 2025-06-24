package com.vinylshop.controller;

import com.vinylshop.dto.GenreDto;
import com.vinylshop.dto.GenreRequest;
import com.vinylshop.mapper.GenreMapper;
import com.vinylshop.service.GenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;
    private final GenreMapper genreMapper;

    @PostMapping("/v1/admin/genres")
    public ResponseEntity<List<GenreDto>> createGenres(
        @RequestBody List<GenreRequest> genreRequest,
        Locale locale
    ) {
        return ResponseEntity.ok(genreService.createFromDto(genreRequest, locale));
    }

    @GetMapping("/v1/genres/{id}")
    public ResponseEntity<GenreDto> getById(
        @PathVariable Long id,
        Locale locale
    ) {
        return ResponseEntity.of(genreService.getById(id)
            .map(x -> genreMapper.toDto(x, locale)));
    }

    @GetMapping("/v1/genres")
    public ResponseEntity<List<GenreDto>> getAll(Locale locale) {
        return ResponseEntity.ok(genreService.getAllLocalizedGenres(locale));
    }

}
