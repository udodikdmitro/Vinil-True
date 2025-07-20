package com.vinylshop.service;

import com.vinylshop.entity.FileData;
import com.vinylshop.entity.FileMetadata;
import com.vinylshop.mapper.FileMetadataMapper;
import com.vinylshop.repository.FileDataRepository;
import com.vinylshop.repository.FileMetadataRepository;
import com.vinylshop.upload.MultipartFileUploadedFileAdapter;
import com.vinylshop.upload.UploadedFileAdapter;
import com.vinylshop.util.HashUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    private final FileDataRepository fileDataRepository;
    private final FileMetadataMapper fileMetadataMapper;

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

    @Transactional
    private List<FileMetadata> saveFiles(Collection<UploadedFileAdapter> uploadedFileAdapters) {
        UriComponentsBuilder uriBuilder = ServletUriComponentsBuilder
            .fromCurrentContextPath()
            .path(fileMetadataEndpoint);

        Map<String, FileMetadata> resultMap = new LinkedHashMap<>();
        List<String> orderedHashes = new ArrayList<>();
        List<FileMetadata> toSave = new ArrayList<>();

        Map<String, UploadedFileAdapter> tempMap = new HashMap<>();
        for (UploadedFileAdapter file : uploadedFileAdapters) {
            try {
                String hash = HashUtils.hashSHA1(file.getBytes());
                orderedHashes.add(hash);
                tempMap.putIfAbsent(hash, file);
            } catch (IOException e) {
                throw new RuntimeException("Failed to process uploaded file", e);
            }
        }

        List<FileMetadata> existing = metadataRepository.findAllByHashIn(tempMap.keySet());
        for (FileMetadata metadata : existing) {
            resultMap.put(metadata.getHash(), metadata);
        }

        for (Map.Entry<String, UploadedFileAdapter> entry : tempMap.entrySet()) {
            String hash = entry.getKey();
            if (resultMap.containsKey(hash)) continue;

            try {
                UploadedFileAdapter file = entry.getValue();
                FileMetadata metadata = fileMetadataMapper.toEntity(file);
                metadata.setHash(hash);
                metadata.setFileData(new FileData(null, file.getBytes(), null));
                toSave.add(metadata);
                resultMap.put(hash, metadata);
            } catch (Exception e) {
                throw new RuntimeException("Failed to process uploaded file", e);
            }
        }

        metadataRepository.saveAll(toSave);

        for (FileMetadata metadata : resultMap.values()) {
            if (metadata.getUrl() == null) {
                URI uri = uriBuilder.buildAndExpand(metadata.getId()).toUri();
                metadata.setUrl(uri.toString());
                metadata.setContentUrl(uri + "/content");
            }
        }

        List<FileMetadata> result = new ArrayList<>(orderedHashes.size());
        for (String hash : orderedHashes) {
            result.add(resultMap.get(hash));
        }

        return result;
    }

    @Transactional(readOnly = true)
    public Optional<FileMetadata> getMetadataById(Long id) {
        return metadataRepository.findById(id);
    }

    @Transactional
    public void deleteFiles(List<Long> ids) {
        fileDataRepository.deleteAllByIdInBatch(ids);
    }

}
