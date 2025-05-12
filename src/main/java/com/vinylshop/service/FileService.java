package com.vinylshop.service;

import com.vinylshop.dto.FileMetadataDto;
import com.vinylshop.entity.FileData;
import com.vinylshop.entity.FileMetadata;
import com.vinylshop.repository.FileMetadataRepository;
import com.vinylshop.upload.MultipartFileUploadedFileAdapter;
import com.vinylshop.upload.UploadedFileAdapter;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.*;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileMetadataRepository metadataRepository;

    @Value("${api.file-metadata.endpoint}")
    private String fileMetadataEndpoint;

    @Transactional
    public List<FileMetadata> saveFilesFromMultipartFiles(Collection<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return Collections.emptyList();
        }

        List<UploadedFileAdapter> uploadedFileAdapters = files.stream()
                .map(file -> (UploadedFileAdapter) new MultipartFileUploadedFileAdapter(file))
                .toList();

        return saveFiles(uploadedFileAdapters);
    }

    @Transactional
    public List<FileMetadata> saveFilesFromUploadedFileAdapters(Collection<UploadedFileAdapter> files) {
        if (files == null || files.isEmpty()) {
            return Collections.emptyList();
        }
        return saveFiles(files);
    }

    private List<FileMetadata> saveFiles(Collection<UploadedFileAdapter> uploadedFileAdapters) {
        UriComponentsBuilder uriBuilder = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(fileMetadataEndpoint);

        List<FileMetadata> metadatas = new ArrayList<>();

        for(UploadedFileAdapter file : uploadedFileAdapters) {
            try {
                FileData fileData = FileData.builder()
                        .bytes(file.getBytes())
                        .build();

                FileMetadata fileMetadata = FileMetadata.builder()
                        .name(file.getName())
                        .originalName(file.getOriginalFilename())
                        .description(file.getOriginalFilename())
                        .contentType(file.getContentType())
                        .size(file.getSize())
                        .fileData(fileData)
                        .build();

                metadatas.add(fileMetadata);
            } catch (IOException e) {
                throw new RuntimeException("Failed to process uploaded file", e);
            }
        }

        metadatas = metadataRepository.saveAll(metadatas);

        for(FileMetadata metadata : metadatas) {
            URI uri = uriBuilder.buildAndExpand(metadata.getId()).toUri();
            metadata.setUrl(uri.toString());
            metadata.setContentUrl(metadata.getUrl() + "/content");
        }

        return metadatas;
    }

    public Optional<FileMetadata> getMetadataById(Long id) {
        return metadataRepository.findById(id);
    }

    @Transactional
    public void deleteFiles(List<Long> ids) {
        metadataRepository.deleteAllByIdInBatch(ids);
    }

    public FileMetadataDto toDto(FileMetadata entity) {
        return new FileMetadataDto(
                entity.getId(),
                entity.getName(),
                entity.getOriginalName(),
                entity.getDescription(),
                entity.getContentType(),
                entity.getUrl(),
                entity.getContentUrl(),
                entity.getSize(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

}
