package com.vinylshop.service;

import com.vinylshop.dto.FileMetadataDto;
import com.vinylshop.dto.VinylDto;
import com.vinylshop.dto.filter.VinylFilter;
import com.vinylshop.entity.FileMetadata;
import com.vinylshop.entity.Genre;
import com.vinylshop.entity.Vinyl;
import com.vinylshop.exception.ResourceNotFoundException;
import com.vinylshop.mapper.FileMetadataMapper;
import com.vinylshop.mapper.VinylMapper;
import com.vinylshop.repository.VinylRepository;
import com.vinylshop.util.ExcelUtil;
import com.vinylshop.upload.UploadedFileAdapter;
import com.vinylshop.util.SpecificationFactory;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static com.vinylshop.util.Constants.DEFAULT_CURRENCY;

@Service
@RequiredArgsConstructor
public class VinylService {

    private final VinylRepository vinylRepository;
    private final FileService fileService;
    private final VinylMapper vinylMapper;
    private final FileMetadataMapper fileMetadataMapper;
    private final GenreService genreService;

    public List<VinylDto> findTop10ForMainPage() {
        return vinylRepository.findTop10ByOrderByYearDesc().stream()
            .map(x -> vinylMapper.toLocalizeDto(x, LocaleContextHolder.getLocale()))
            .toList();
    }

    @Transactional
    public VinylDto saveFromDto(VinylDto dto, Collection<MultipartFile> files) {
        Vinyl vinyl = vinylMapper.toEntity(dto);

        if (dto.getGenre() != null && dto.getGenre().id() != null) {
            Genre genre = genreService.getByIdOrThrow(dto.getGenre().id());
            vinyl.setGenre(genre);
        }
        if (vinyl.getTitle() == null) {
            vinyl.setTitle(vinyl.getAlbum());
        }
        if (dto.getQuantity() == null) {
            vinyl.setQuantity(1);
        }
        vinyl.setCurrency(DEFAULT_CURRENCY);

        if (files != null && !files.isEmpty()) {
            vinyl.setImages(fileService.saveFilesFromMultipartFiles(files));
        }

        vinyl = vinylRepository.save(vinyl);

        return vinylMapper.toLocalizeDto(vinyl, LocaleContextHolder.getLocale());
    }

    @Transactional
    public Vinyl save(Vinyl vinyl, Collection<UploadedFileAdapter> images) {
        if (vinyl.getGenre() != null && vinyl.getGenre().getId() != null) {
            Genre genre = genreService.getByIdOrThrow(vinyl.getGenre().getId());
            vinyl.setGenre(genre);
        }
        if (vinyl.getTitle() == null) {
            vinyl.setTitle(vinyl.getAlbum());
        }
        if (vinyl.getQuantity() == 0) {
            vinyl.setQuantity(1);
        }
        vinyl.setCurrency(DEFAULT_CURRENCY);
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

    @Transactional(readOnly = true)
    public Page<VinylDto> findAll(VinylFilter filter, Pageable pageable) {
        Specification<Vinyl> specification = SpecificationFactory.create(filter);
        return vinylRepository.findAll(specification, pageable)
            .map(x -> vinylMapper.toLocalizeDto(x, LocaleContextHolder.getLocale()));
    }

}

