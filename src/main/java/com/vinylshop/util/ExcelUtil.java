package com.vinylshop.util;

import com.vinylshop.entity.Vinyl;
import com.vinylshop.upload.SsPictureDataUploadedFileAdapter;
import com.vinylshop.upload.UploadedFileAdapter;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.PictureData;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.BiConsumer;

public final class ExcelUtil {
    private ExcelUtil() {}

    private static final Logger log = LoggerFactory.getLogger(ExcelUtil.class);

    private record CellHandler(int index, CellType expectedType, BiConsumer<Vinyl, Cell> setter) {}

    private static final List<CellHandler> cellVinylHandlers = List.of(
            new CellHandler(0, CellType.STRING, (vinyl, cell) -> vinyl.setTitle(cell.getStringCellValue())),
            new CellHandler(1, CellType.STRING, (vinyl, cell) -> vinyl.setArtist(cell.getStringCellValue())),
            new CellHandler(2, CellType.NUMERIC, (vinyl, cell) -> vinyl.setYear((int) cell.getNumericCellValue())),
            new CellHandler(3, CellType.NUMERIC, (vinyl, cell) -> vinyl.setPrice(BigDecimal.valueOf(cell.getNumericCellValue()))),
            new CellHandler(4, CellType.NUMERIC, (vinyl, cell) -> vinyl.setQuantity((int) cell.getNumericCellValue()))
    );

    public static Vinyl getVinylFromRow(Row row) {
        final Vinyl vinyl = new Vinyl();
        for (CellHandler handler : cellVinylHandlers) {
            checkCellType(row, handler.index(), handler.expectedType());
            Cell cell = row.getCell(handler.index());
            handler.setter().accept(vinyl, cell);
        }
        return vinyl;
    }

    public static void checkCellType(Row row, int cellIndex, CellType expectedType)
            throws NoSuchElementException, IllegalStateException {
        String cellString = new CellReference(row.getRowNum(), cellIndex).formatAsString();

        if (cellIndex >= row.getLastCellNum()) {
            throw new NoSuchElementException("Cell not found at " + cellString);
        }

        Cell cell = row.getCell(cellIndex);
        if (cell == null) {
            throw new NoSuchElementException("Cell is null at " + cellString);
        }

        CellType actualType = cell.getCellType();
        if (actualType == CellType.FORMULA) {
            actualType = cell.getCachedFormulaResultType();
        }

        if (actualType != expectedType) {
            log.warn("Expected cell type {} at {}, but found {}", expectedType, cellString, actualType);
            throw new IllegalStateException("Invalid cell type at " + cellString + ": expected " + expectedType + ", but was " + actualType);
        }
    }

    public static Map<Integer, List<UploadedFileAdapter>> extractPictureData(XSSFSheet sheet) {
        Map<Integer, List<UploadedFileAdapter>> rowImageMap = new HashMap<>();

        for (POIXMLDocumentPart part : sheet.getRelations()) {
            if (part instanceof XSSFDrawing drawing) {
                for (XSSFShape shape : drawing.getShapes()) {
                    if (shape instanceof XSSFPicture picture) {
                        XSSFClientAnchor anchor = picture.getPreferredSize();
                        int row = anchor.getRow1();

                        List<UploadedFileAdapter> imagesForRow = rowImageMap.computeIfAbsent(row, k -> new ArrayList<>());
                        PictureData pictureData = picture.getPictureData();
                        UploadedFileAdapter adapter = new SsPictureDataUploadedFileAdapter(pictureData);
                        imagesForRow.add(adapter);
                    }
                }
            }
        }

        return rowImageMap;
    }
}