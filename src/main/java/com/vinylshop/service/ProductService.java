package com.vinylshop.service;

import com.vinylshop.dto.ProductUpdateRequest;
import com.vinylshop.dto.FileMetadataDto;
import com.vinylshop.entity.FileMetadata;
import com.vinylshop.dto.PageDto;
import com.vinylshop.dto.ProductDto;
import com.vinylshop.dto.SearchResponse;
import com.vinylshop.dto.filter.*;
import com.vinylshop.entity.Product;
import com.vinylshop.exception.ResourceNotFoundException;
import com.vinylshop.mapper.FileMetadataMapper;
import com.vinylshop.repository.ProductRepository;
import com.vinylshop.upload.MultipartFileUploadedFileAdapter;
import com.vinylshop.upload.UploadedFileAdapter;
import com.vinylshop.util.HashUtils;
import com.vinylshop.repository.ProductProjectionRepository;
import com.vinylshop.util.SpecificationFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.query.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.vinylshop.util.Constants.BIG_DECIMAL_EMPTY_VALUE;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final FileService fileService;
    private final FileMetadataMapper fileMetadataMapper;
    private final ProductProjectionRepository productProjectionRepository;

    @Transactional(readOnly = true)
    public Product getByIdOrThrow(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product " + id + " not found", id, "Product"));
    }

    @Transactional(readOnly = true)
    public SearchResponse search(GlobalProductFilter filter, Pageable pageable) {
        VinylFilter vinylFilter = VinylFilterImpl.from(filter);
        PageDto<ProductDto> vinylPageDto = searchProducts(vinylFilter, pageable);

        GiftCertificateFilter giftCertificateFilter = GiftCertificateFilterImpl.from(filter);
        PageDto<ProductDto> giftCertificatePageDto = searchProducts(giftCertificateFilter, pageable);

        return new SearchResponse(vinylPageDto, giftCertificatePageDto);
    }

    @Transactional(readOnly = true)
    public PageDto<ProductDto> searchProducts(ProductFilter filter, Pageable pageable) {
        Pageable tPageable = pageable;
        if (filter.popularFirst() != null && filter.popularFirst()) {
            Sort sort = Sort.by(Sort.Direction.DESC, "viewsCount")
                .and(pageable.getSort());
            tPageable = PageRequest.of(pageable.getPageNumber(),
                pageable.getPageSize(), sort);
        }

        Specification<Product> spec = SpecificationFactory.createForProduct(filter);
        Page<ProductDto> page = productProjectionRepository.findAllProjected(spec, tPageable);
        return new PageDto<>(page);
    }

    @Transactional
    public List<FileMetadataDto> addImagesFromMultipartFiles(Long id, List<MultipartFile> files) {
        Product product = getByIdOrThrow(id);

        List<UploadedFileAdapter> uploadedFileAdapters = files.stream()
            .map(file -> (UploadedFileAdapter) new MultipartFileUploadedFileAdapter(file))
            .toList();

        List<FileMetadata> images = addImages(product, uploadedFileAdapters);

        return fileMetadataMapper.toDtoAll(images).toList();
    }

    @Transactional
    public List<FileMetadata> addImages(Product product, List<UploadedFileAdapter> files) {
        List<FileMetadata> images = product.getImages();

        Set<String> existingHashes = images.stream()
            .map(FileMetadata::getHash)
            .collect(Collectors.toSet());

        List<UploadedFileAdapter> toSaveFiles = files.stream()
            .filter(file -> {
                try {
                    return !existingHashes.contains(HashUtils.hashSHA1(file.getBytes()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            })
            .toList();

        if (!toSaveFiles.isEmpty()) {
            List<FileMetadata> savedImages = fileService.saveFilesFromUploadedFileAdapters(toSaveFiles);
            images.addAll(savedImages);
            product = productRepository.save(product);
        }

        return product.getImages();
    }

    @Transactional
    public void removeImages(Long id, List<Long> fileIds) {
        if (fileIds != null && !fileIds.isEmpty()) {
            fileService.deleteReferenceToProduct(id, fileIds);
        }
    }

    public void processDiscount(Product product, ProductUpdateRequest request) {
        BigDecimal reqDiscountPrice = request.getDiscountPrice();
        BigDecimal reqDiscountValue = request.getDiscountValue();
        BigDecimal reqPrice = request.getPrice();

        if (reqPrice == null && reqDiscountValue == null &&
            reqDiscountPrice == BIG_DECIMAL_EMPTY_VALUE) {
            return;
        }

        if (reqDiscountPrice != BIG_DECIMAL_EMPTY_VALUE && reqDiscountValue != null) {
            throw new IllegalStateException("Illegal state");
        }

        if (reqPrice != null) {
            product.setPrice(reqPrice);
        }

        BigDecimal productPrice = product.getPrice();

        if (reqDiscountPrice != null) {
            if (BigDecimal.ZERO.compareTo(reqDiscountPrice) == 0) {
                product.setDiscountPrice(null);
                product.setDiscountValue(BigDecimal.ZERO);
            } else {
                product.setDiscountPrice(reqDiscountPrice.setScale(2, RoundingMode.HALF_UP));
                product.setDiscountValue(calculateDiscount(productPrice, reqDiscountPrice));
            }
        }

        if (reqDiscountValue != null) {
            if (BigDecimal.ZERO.compareTo(reqDiscountValue) == 0) {
                product.setDiscountValue(BigDecimal.ZERO);
                product.setDiscountPrice(null);
            } else {
                product.setDiscountValue(reqDiscountValue.setScale(2, RoundingMode.HALF_UP));
                product.setDiscountPrice(calculateDiscountPrice(productPrice, reqDiscountValue));
            }
        }
    }

    private BigDecimal calculateDiscount(BigDecimal price, BigDecimal discountPrice) {
        return price.subtract(discountPrice)
            .divide(price, 10, RoundingMode.HALF_UP)
            .multiply(BigDecimal.valueOf(100))
            .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateDiscountPrice(BigDecimal price, BigDecimal discount) {
        BigDecimal discountFraction = discount.divide(BigDecimal.valueOf(100), 10,
            RoundingMode.HALF_UP);
        discountFraction = BigDecimal.ONE.subtract(discountFraction);
        return price.multiply(discountFraction).setScale(2, RoundingMode.HALF_UP);
    }

}
