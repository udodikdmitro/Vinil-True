package com.vinylshop.service;

import com.vinylshop.dto.VinylDto;
import com.vinylshop.entity.Vinyl;
import com.vinylshop.repository.VinylRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VinylService {

    private final VinylRepository vinylRepository;

    public List<VinylDto> findTop10ForMainPage() {
        return vinylRepository.findTop10ByOrderByYearDesc().stream()
                .map(v -> new VinylDto(v.getId(), v.getTitle(), v.getArtist(), v.getYear(), v.getImage()))
                .toList();
    }

    public VinylDto save(VinylDto dto) {
        Vinyl vinyl = new Vinyl();
        vinyl.setTitle(dto.getTitle());
        vinyl.setArtist(dto.getArtist());
        vinyl.setYear(dto.getYear());
        vinyl.setImage(dto.getImage());
        Vinyl saved = vinylRepository.save(vinyl);
        return new VinylDto(saved.getId(), saved.getTitle(), saved.getArtist(), saved.getYear(), saved.getImage());
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

    public Optional<VinylDto> findById(Long id) {
        return vinylRepository.findById(id)
                .map(entity -> new VinylDto(
                    entity.getId(),
                    entity.getTitle(),
                    entity.getArtist(),
                    entity.getYear(),
                    entity.getImage()
                ));
    }
}

