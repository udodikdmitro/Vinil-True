package com.vinylshop.controller;

import com.vinylshop.dto.VinylDto;
import com.vinylshop.service.VinylService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @GetMapping
    public VinylDto getVinyl(@RequestParam("id") String id) {}

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VinylDto> create(@RequestBody VinylDto dto) {
        return ResponseEntity.ok(vinylService.save(dto));
    }

    @PostMapping("/import")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> importExcel(@RequestParam("file") MultipartFile file) {
        vinylService.importFromExcel(file);
        return ResponseEntity.ok("Імпортовано");
    }
}

