package com.vinylshop.service;

import com.vinylshop.dto.FileMetadataDto;
import com.vinylshop.dto.VinylDto;
import com.vinylshop.entity.FileMetadata;
import com.vinylshop.entity.Vinyl;
import com.vinylshop.exception.ResourceNotFoundException;
import com.vinylshop.mapper.FileMetadataMapper;
import com.vinylshop.mapper.VinylMapper;
import com.vinylshop.repository.VinylRepository;
import com.vinylshop.util.ExcelUtil;
import com.vinylshop.upload.UploadedFileAdapter;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
@RequiredArgsConstructor
public class VinylService {

    private final VinylRepository vinylRepository;
    private final FileService fileService;
    private final VinylMapper vinylMapper;
    private final FileMetadataMapper fileMetadataMapper;

    public List<VinylDto> findTop10ForMainPage() {
        return vinylMapper.toDtoAll(vinylRepository.findTop10ByOrderByYearDesc())
                .toList();
    }

    @Transactional
    public VinylDto saveFromDto(VinylDto dto, Collection<MultipartFile> files) {
        Vinyl vinyl = vinylMapper.toEntity(dto);

        if (files != null && !files.isEmpty()) {
            vinyl.setImages(fileService.saveFilesFromMultipartFiles(files));
        }

        vinyl = vinylRepository.save(vinyl);

        return vinylMapper.toDto(vinyl);
    }

    @Transactional
    public Vinyl save(Vinyl vinyl, Collection<UploadedFileAdapter> images) {
        vinyl.setImages(fileService.saveFilesFromUploadedFileAdapters(images));
        return vinylRepository.save(vinyl);
    }

    @Transactional
    public List<FileMetadataDto> addImagesFromMultipartFiles(Long id, List<MultipartFile> files) {
        Vinyl vinyl = vinylRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("vinyl not found", id, "Vinyl"));

        vinyl.getImages().addAll(fileService.saveFilesFromMultipartFiles(files));
        vinyl = vinylRepository.save(vinyl);

        return fileMetadataMapper.toDtoAll(vinyl.getImages()).toList();
    }

    @Transactional
    public List<FileMetadata> addImagesFromUploadedFileAdapters(Long id, List<UploadedFileAdapter> files) {
        Vinyl vinyl = vinylRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("vinyl not found", id, "Vinyl"));
        vinyl.getImages().addAll(fileService.saveFilesFromUploadedFileAdapters(files));
        vinyl = vinylRepository.save(vinyl);
        return vinyl.getImages();
    }

    @Transactional
    public List<FileMetadata> removeImages(Long id, List<Long> fileIds) {
        Vinyl vinyl = vinylRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("vinyl not found", id, "Vinyl"));

        vinyl.getImages().removeIf(x -> fileIds.contains(x.getId()));

        fileService.deleteFiles(fileIds);

        vinyl = vinylRepository.save(vinyl);
        return vinyl.getImages();
    }

    @Transactional
    public void importFromExcel(MultipartFile file) {
        try (InputStream is = file.getInputStream(); XSSFWorkbook workbook = new XSSFWorkbook(is)) {
            XSSFSheet sheet = workbook.getSheetAt(0);
            Map<Integer, List<UploadedFileAdapter>> imageMap = ExcelUtil.extractPictureData(sheet);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                Vinyl vinyl = ExcelUtil.getVinylFromRow(row);
                List<UploadedFileAdapter> images = imageMap.getOrDefault(row.getRowNum(), Collections.emptyList());
                save(vinyl, images);
            }
        } catch (IOException e) {
            throw new RuntimeException("Помилка імпорту", e);
        }
    }

    public Optional<Vinyl> findById(Long id) {
        return vinylRepository.findById(id);
    }

}

