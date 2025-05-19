package com.vinylshop.service;

import com.vinylshop.dto.FileMetadataDto;
import com.vinylshop.dto.VinylDto;
import com.vinylshop.entity.FileMetadata;
import com.vinylshop.entity.Vinyl;
import com.vinylshop.exception.ResourceNotFoundException;
import com.vinylshop.mapper.FileMetadataMapper;
import com.vinylshop.repository.VinylRepository;
import com.vinylshop.upload.SsPictureDataUploadedFileAdapter;
import com.vinylshop.upload.UploadedFileAdapter;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.ss.usermodel.PictureData;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
@RequiredArgsConstructor
public class VinylService {

    private final VinylRepository vinylRepository;
    private final FileService fileService;
    private final FileMetadataMapper fileMetadataMapper;

    public List<VinylDto> findTop10ForMainPage() {
        return vinylRepository.findTop10ByOrderByYearDesc().stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public VinylDto saveFromDto(VinylDto dto, Collection<MultipartFile> files) {
        Vinyl vinyl = new Vinyl();
        vinyl.setTitle(dto.getTitle());
        vinyl.setArtist(dto.getArtist());
        vinyl.setYear(dto.getYear());

        if (files != null && !files.isEmpty()) {
            vinyl.setImages(fileService.saveFilesFromMultipartFiles(files));
        }

        vinyl = vinylRepository.save(vinyl);

        return toDto(vinyl);
    }

    @Transactional
    public Vinyl save(Vinyl vinyl, Collection<UploadedFileAdapter> images) {
        vinyl.setImages(fileService.saveFilesFromUploadedFileAdapters(images));
        return vinylRepository.save(vinyl);
    }

    @Transactional
    public List<FileMetadataDto> addImagesFromMultipartFiles(Long id, List<MultipartFile> files) {
        Vinyl vinyl = vinylRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("vinil not found"));

        vinyl.getImages().addAll(fileService.saveFilesFromMultipartFiles(files));
        vinyl = vinylRepository.save(vinyl);

        return fileMetadataMapper.toDtoAll(vinyl.getImages()).toList();
    }

    @Transactional
    public List<FileMetadata> addImagesFromUploadedFileAdapters(Long id, List<UploadedFileAdapter> files) {
        Vinyl vinyl = vinylRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("vinil not found"));
        vinyl.getImages().addAll(fileService.saveFilesFromUploadedFileAdapters(files));
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
        try (InputStream is = file.getInputStream(); XSSFWorkbook workbook = new XSSFWorkbook(is)) {
            XSSFSheet sheet = workbook.getSheetAt(0);
            Map<Integer, List<UploadedFileAdapter>> imageMap = extractPictureData(sheet);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                Vinyl vinyl = new Vinyl();
                vinyl.setTitle(row.getCell(0).getStringCellValue());
                vinyl.setArtist(row.getCell(1).getStringCellValue());
                vinyl.setYear((int) row.getCell(2).getNumericCellValue());

                List<UploadedFileAdapter> images = imageMap.getOrDefault(row.getRowNum(), Collections.emptyList());
                save(vinyl, images);
            }
        } catch (IOException e) {
            throw new RuntimeException("Помилка імпорту", e);
        }
    }

    private Map<Integer, List<UploadedFileAdapter>> extractPictureData(XSSFSheet sheet) {
        Map<Integer, List<UploadedFileAdapter>> colImageMap = new HashMap<>();

        for (POIXMLDocumentPart part : sheet.getRelations()) {
            if (part instanceof XSSFDrawing) {
                XSSFDrawing drawing = (XSSFDrawing) part;
                for (XSSFShape shape : drawing.getShapes()) {
                    if (shape instanceof XSSFPicture) {
                        XSSFPicture picture = (XSSFPicture) shape;
                        XSSFClientAnchor anchor = picture.getPreferredSize();

                        int row = anchor.getRow1();

                        List<UploadedFileAdapter> colImages = colImageMap.computeIfAbsent(row, (k) -> new ArrayList<>());

                        PictureData pictureData = picture.getPictureData();
                        UploadedFileAdapter uploadedFileAdapter = new SsPictureDataUploadedFileAdapter(pictureData);
                        colImages.add(uploadedFileAdapter);
                    }
                }
            }
        }

        return colImageMap;
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

