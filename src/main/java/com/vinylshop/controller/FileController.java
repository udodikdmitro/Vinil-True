package com.vinylshop.controller;

import com.vinylshop.dto.FileMetadataDto;
import com.vinylshop.entity.FileData;
import com.vinylshop.entity.FileMetadata;
import com.vinylshop.mapper.FileMetadataMapper;
import com.vinylshop.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;
    private final FileMetadataMapper fileMetadataMapper;

    @GetMapping("/{id}")
    private ResponseEntity<FileMetadataDto> getFileById(@PathVariable Long id) {
        return ResponseEntity.of(fileService.getMetadataById(id)
                .map(fileMetadataMapper::toDto));
    }

    @GetMapping("/{id}/content")
    private ResponseEntity<Resource> getFileContentById(@PathVariable Long id) {
        Optional<FileMetadata> metadataOpt = fileService.getMetadataById(id);
        if (metadataOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        FileMetadata metadata = metadataOpt.get();
        FileData data = metadata.getFileData();
        if (data == null || data.getBytes() == null) {
            return ResponseEntity.unprocessableEntity().build();
        }

        Resource fileResource = new ByteArrayResource(data.getBytes(), metadata.getDescription());
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(metadata.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + metadata.getOriginalName() + "\"")
                .body(fileResource);
    }

}
