package com.vinylshop.service;

import com.vinylshop.dto.FileMetadataDto;
import com.vinylshop.dto.VinylDto;
import com.vinylshop.entity.FileMetadata;
import com.vinylshop.entity.Vinyl;
import com.vinylshop.exception.InsufficientStockException;
import com.vinylshop.exception.ResourceNotFoundException;
import com.vinylshop.mapper.FileMetadataMapper;
import com.vinylshop.mapper.VinylMapper;
import com.vinylshop.repository.VinylRepository;
import com.vinylshop.util.Constants;
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
import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class VinylService {

    private final VinylRepository vinylRepository;
    private final FileService fileService;
    private final VinylMapper vinylMapper;
    private final FileMetadataMapper fileMetadataMapper;
    private final CurrencyConversionService conversionService;

    public List<VinylDto> findTop10ForMainPage() {
        return vinylMapper.toDtoAll(vinylRepository.findTop10ByOrderByYearDesc().stream()
                        .map(this::convertPriceToPreferred)
                        .toList())
                .toList();
    }

    @Transactional
    public VinylDto saveFromDto(VinylDto dto, Collection<MultipartFile> files) {
        Vinyl vinyl = vinylMapper.toEntity(dto);

        convertPriceToDefault(vinyl);

        if (files != null && !files.isEmpty()) {
            vinyl.setImages(fileService.saveFilesFromMultipartFiles(files));
        }

        vinyl = vinylRepository.save(vinyl);

        return vinylMapper.toDto(vinyl);
    }

    @Transactional
    public Vinyl save(Vinyl vinyl, Collection<UploadedFileAdapter> images) {
        convertPriceToDefault(vinyl);
        vinyl.setImages(fileService.saveFilesFromUploadedFileAdapters(images));
        return vinylRepository.save(vinyl);
    }

    @Transactional
    public List<FileMetadataDto> addImagesFromMultipartFiles(Long id, List<MultipartFile> files) {
        Vinyl vinyl = findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("vinyl not found", id, "Vinyl"));

        vinyl.getImages().addAll(fileService.saveFilesFromMultipartFiles(files));
        vinyl = vinylRepository.save(vinyl);

        return fileMetadataMapper.toDtoAll(vinyl.getImages()).toList();
    }

    @Transactional
    public List<FileMetadata> addImagesFromUploadedFileAdapters(Long id, List<UploadedFileAdapter> files) {
        Vinyl vinyl = findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("vinyl not found", id, "Vinyl"));
        vinyl.getImages().addAll(fileService.saveFilesFromUploadedFileAdapters(files));
        vinyl = vinylRepository.save(vinyl);
        return vinyl.getImages();
    }

    @Transactional
    public List<FileMetadata> removeImages(Long id, List<Long> fileIds) {
        Vinyl vinyl = findById(id)
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
        return vinylRepository.findById(id).map(this::convertPriceToPreferred);
    }

    public void checkVinylQuantity(Vinyl vinyl, int quantity) throws InsufficientStockException {
        final int vinylQuantity = vinyl.getQuantity();
        if (vinylQuantity == 0) {
            throw new InsufficientStockException(
                    "Item is out of stock and cannot be added to the cart.",
                    vinyl.getId(), "Vinyl", 0);
        } else if (vinylQuantity < quantity) {
            throw new InsufficientStockException(
                    "Cannot add more items. Only " + vinyl.getQuantity() + " units available in stock.",
                    vinyl.getId(), "Vinyl", vinyl.getQuantity());
        }
    }

    public Vinyl convertPriceToDefault(Vinyl vinyl) {
        final BigDecimal price = vinyl.getPrice();
        final Currency originalCurrency = vinyl.getCurrency();

        vinyl.setOriginalPrice(price);
        vinyl.setOriginalCurrency(originalCurrency);

        final BigDecimal convertedPrice = conversionService.convert(price, originalCurrency, Constants.DEFAULT_CURRENCY);
        vinyl.setPrice(convertedPrice);
        vinyl.setCurrency(Constants.DEFAULT_CURRENCY);
        return vinyl;
    }

    public Vinyl convertPriceToPreferred(Vinyl vinyl) {
        final Currency toCurrency = PreferredCurrencyHolder.getCurrency();
        final Currency fromCurrency = vinyl.getCurrency();
        final BigDecimal convertedPrice = conversionService.convert(vinyl.getPrice(), fromCurrency, toCurrency);
        vinyl.setPrice(convertedPrice);
        vinyl.setCurrency(toCurrency);
        return vinyl;
    }

    public VinylDto convertPriceToPreferred(VinylDto vinyl) {
        final Currency toCurrency = PreferredCurrencyHolder.getCurrency();
        final Currency fromCurrency = Currency.getInstance(vinyl.getCurrency());
        final BigDecimal convertedPrice = conversionService.convert(vinyl.getPrice(), fromCurrency, toCurrency);
        vinyl.setPrice(convertedPrice);
        vinyl.setCurrency(toCurrency.getCurrencyCode());
        return vinyl;
    }

    public Vinyl convertPriceToOriginal(Vinyl vinyl) {
        final Currency fromCurrency = vinyl.getCurrency();
        final Currency originalCurrency = vinyl.getOriginalCurrency();
        final BigDecimal convertedPrice = conversionService.convert(vinyl.getPrice(), fromCurrency, originalCurrency);
        vinyl.setPrice(convertedPrice);
        vinyl.setCurrency(originalCurrency);
        return vinyl;
    }

}

