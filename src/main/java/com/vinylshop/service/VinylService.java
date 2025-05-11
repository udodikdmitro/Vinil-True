package com.vinylshop.service;

import com.vinylshop.dto.VinylDto;
import com.vinylshop.entity.FileMetadata;
import com.vinylshop.entity.Vinyl;
import com.vinylshop.exception.ResourceNotFoundException;
import com.vinylshop.repository.VinylRepository;
import com.vinylshop.upload.UploadedFileAdapter;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.function.Predicate.isEqual;

@Service
@RequiredArgsConstructor
public class VinylService {

    private final VinylRepository vinylRepository;
    private final FileService fileService;

    public List<VinylDto> findTop10ForMainPage() {
        return vinylRepository.findTop10ByOrderByYearDesc().stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public Vinyl create(Vinyl vinyl, Collection<UploadedFileAdapter> images) {
        vinyl.setImages(fileService.createFiles(images));
        return create(vinyl);
    }

    @Transactional
    public Vinyl create(Vinyl vinyl) {
        return vinylRepository.save(vinyl);
    }

    @Transactional
    public List<FileMetadata> addImages(Long id, List<UploadedFileAdapter> files) {
        Vinyl vinyl = vinylRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("vinil not found"));
        vinyl.getImages().addAll(fileService.createFiles(files));
        vinyl = vinylRepository.save(vinyl);
        return vinyl.getImages();
    }

    @Transactional
    public List<FileMetadata> removeImages(Long id, List<Long> fileIds) {
        Vinyl vinyl = vinylRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("vinil not found"));

        vinyl.getImages().removeIf(x -> fileIds.contains(x.getId()));

        fileService.deleteFiles(fileIds);

        vinyl = vinylRepository.save(vinyl);
        return vinyl.getImages();
    }

    public void importFromExcel(MultipartFile file) {
        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                Vinyl vinyl = new Vinyl();
                vinyl.setTitle(row.getCell(0).getStringCellValue());
                vinyl.setArtist(row.getCell(1).getStringCellValue());
                vinyl.setYear((int) row.getCell(2).getNumericCellValue());
                vinylRepository.save(vinyl);
            }
        } catch (IOException e) {
            throw new RuntimeException("Помилка імпорту", e);
        }
    }

    public Optional<Vinyl> findById(Long id) {
        return vinylRepository.findById(id);
    }

    public VinylDto toDto(Vinyl entity) {
        return new VinylDto(
                entity.getId(),
                entity.getTitle(),
                entity.getArtist(),
                entity.getYear(),
                entity.getImages()
                        .stream()
                        .map(FileMetadata::getContentUrl)
                        .toList()
        );
    }

}

