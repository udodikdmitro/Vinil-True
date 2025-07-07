package com.vinylshop.service;

import com.vinylshop.dto.ProductUpdateRequest;
import com.vinylshop.entity.Product;
import com.vinylshop.exception.ResourceNotFoundException;
import com.vinylshop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.vinylshop.util.Constants.BIG_DECIMAL_EMPTY_VALUE;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public Product getByIdOrThrow(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product " + id + " not found", id, "Product"));
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
