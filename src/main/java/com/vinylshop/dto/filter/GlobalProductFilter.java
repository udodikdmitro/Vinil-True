package com.vinylshop.dto.filter;

import com.vinylshop.entity.ProductType;
import com.vinylshop.entity.ReleaseType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GlobalProductFilter implements GiftCertificateFilter, VinylFilter {
    private String search;
    private Long genreId;
    private String artist;
    private String album;
    private BigDecimal priceFrom;
    private BigDecimal priceTo;
    private Integer yearFrom;
    private Integer yearTo;
    private List<ReleaseType> releaseTypes;
    private ProductType type;

    @Override
    public String search() {
        return search;
    }

    public void search(String search) {
        this.search = search;
    }

    @Override
    public Long genreId() {
        return genreId;
    }

    public void genreId(Long genreId) {
        this.genreId = genreId;
    }

    @Override
    public String artist() {
        return artist;
    }

    public void artist(String artist) {
        this.artist = artist;
    }

    @Override
    public String album() {
        return album;
    }

    public void album(String album) {
        this.album = album;
    }

    @Override
    public BigDecimal priceFrom() {
        return priceFrom;
    }

    public void priceFrom(BigDecimal priceFrom) {
        this.priceFrom = priceFrom;
    }

    @Override
    public BigDecimal priceTo() {
        return priceTo;
    }

    public void priceTo(BigDecimal priceTo) {
        this.priceTo = priceTo;
    }

    @Override
    public Integer yearFrom() {
        return yearFrom;
    }

    public void yearFrom(Integer yearFrom) {
        this.yearFrom = yearFrom;
    }

    @Override
    public Integer yearTo() {
        return yearTo;
    }

    public void yearTo(Integer yearTo) {
        this.yearTo = yearTo;
    }

    @Override
    public List<ReleaseType> releaseTypes() {
        return releaseTypes;
    }

    public void releaseType(List<ReleaseType> releaseTypes) {
        this.releaseTypes = releaseTypes;
    }

    @Override
    public ProductType type() {
        return type;
    }

    public void type(ProductType type) {
        this.type = type;
    }

}
