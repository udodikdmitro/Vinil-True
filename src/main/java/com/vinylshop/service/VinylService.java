package com.vinylshop.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.vinylshop.dto.VinylDto;
import com.vinylshop.dto.VinylUpdateRequest;
import com.vinylshop.dto.filter.VinylFilter;
import com.vinylshop.entity.FileMetadata;
import com.vinylshop.entity.Genre;
import com.vinylshop.entity.ProductType;
import com.vinylshop.entity.Vinyl;
import com.vinylshop.exception.ResourceNotFoundException;
import com.vinylshop.mapper.VinylMapper;
import com.vinylshop.repository.VinylRepository;
import com.vinylshop.upload.MultipartFileUploadedFileAdapter;
import com.vinylshop.upload.SsPictureDataUploadedFileAdapter;
import com.vinylshop.upload.UploadedFileAdapter;
import com.vinylshop.util.SpecificationFactory;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.ss.usermodel.PictureData;
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
import java.math.BigDecimal;
import java.util.*;

import static com.vinylshop.util.Constants.DEFAULT_CURRENCY;

@Service
@RequiredArgsConstructor
public class VinylService {

    private final VinylRepository vinylRepository;
    private final FileService fileService;
    private final VinylMapper vinylMapper;
    private final GenreService genreService;
    private final ProductService productService;
    private final DeezerService deezerService;

    public List<VinylDto> findTop10ForMainPage() {
        return vinylRepository.findTop10ByOrderByYearDesc().stream()
            .map(x -> vinylMapper.toLocalizeDto(x, LocaleContextHolder.getLocale()))
            .toList();
    }

    @Transactional
    public VinylDto saveFromDto(VinylDto dto, Collection<MultipartFile> files) {
        Vinyl vinyl = vinylMapper.toEntity(dto);
        vinyl.setGenre(new Genre(dto.getGenre().id(), null, null));

        List<UploadedFileAdapter> uploadedFileAdapters = files.stream()
            .map(file -> (UploadedFileAdapter) new MultipartFileUploadedFileAdapter(file))
            .toList();

        Vinyl created = save(vinyl, uploadedFileAdapters);

        return vinylMapper.toLocalizeDto(created, LocaleContextHolder.getLocale());
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
        if (vinyl.getSubtitle() == null) {
            vinyl.setSubtitle(vinyl.getArtist());
        }
        if (vinyl.getQuantity() == 0) {
            vinyl.setQuantity(1);
        }
        if (vinyl.getExternalAlbumId() != null) {
            Optional<JsonNode> albumObjectOpt = deezerService.getAlbumObjectById(vinyl.getExternalAlbumId());
            if (albumObjectOpt.isPresent()) {
                vinyl.setExternalAlbumId(vinyl.getExternalAlbumId());
            }
        }
        if (vinyl.getExternalAlbumId() == null) {
            deezerService.getAlbumObjectByAlbumAndArtist(vinyl.getAlbum(), vinyl.getArtist())
                .flatMap(x -> Optional.ofNullable(x.get("id")))
                .map(JsonNode::asLong)
                .ifPresent(vinyl::setExternalAlbumId);
        }
        vinyl.setDtype(ProductType.VINYL);
        vinyl.setCurrency(DEFAULT_CURRENCY);

        if (images != null && !images.isEmpty()) {
            List<FileMetadata> savedImages = fileService.saveFilesFromUploadedFileAdapters(images);
            vinyl.setImages(savedImages);
            vinyl.setMainImageUrl(savedImages.get(0).getContentUrl());
        }

        return vinylRepository.save(vinyl);
    }

    @Transactional
    public void importFromExcel(MultipartFile file) {
        try (InputStream is = file.getInputStream(); XSSFWorkbook workbook = new XSSFWorkbook(is)) {
            XSSFSheet sheet = workbook.getSheetAt(0);
            Map<Integer, List<UploadedFileAdapter>> imageMap = extractPictureData(sheet);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                Vinyl vinyl = new Vinyl();
                vinyl.setArtist(row.getCell(0).getStringCellValue());
                vinyl.setAlbum(row.getCell(1).getStringCellValue());
                vinyl.setYear((int) row.getCell(2).getNumericCellValue());
                vinyl.setCountryOfOrigin(row.getCell(3).getStringCellValue());
                vinyl.setCatalogCode(row.getCell(4).getStringCellValue());
                vinyl.setLabel(row.getCell(5).getStringCellValue());
                vinyl.setCondition(row.getCell(6).getStringCellValue());
                vinyl.setEnvelopeCondition(row.getCell(7).getStringCellValue());
                vinyl.setPrice(BigDecimal.valueOf(row.getCell(8).getNumericCellValue()));
                vinyl.setNote(row.getCell(9).getStringCellValue());
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

    @Transactional(readOnly = true)
    public Optional<Vinyl> findById(Long id) {
        return vinylRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Page<VinylDto> findAll(VinylFilter filter, Pageable pageable) {
        Specification<Vinyl> specification = SpecificationFactory.create(filter);
        return vinylRepository.findAll(specification, pageable)
            .map(x -> vinylMapper.toLocalizeDto(x, LocaleContextHolder.getLocale()));
    }

    @Transactional
    public Vinyl updateById(Long id, VinylUpdateRequest updateRequest) {
        Vinyl found = vinylRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("vinyl not found", id, "Vinyl"));

        if (updateRequest.getGenreId() != null) {
            Genre genre = genreService.getByIdOrThrow(updateRequest.getGenreId());
            found.setGenre(genre);
        }

        vinylMapper.updateNonNullFields(updateRequest, found);
        productService.processDiscount(found, updateRequest);

        return vinylRepository.save(found);
    }

}

