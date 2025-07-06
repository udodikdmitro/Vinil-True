package com.vinylshop.service;

import com.vinylshop.entity.Product;
import com.vinylshop.exception.InsufficientStockException;
import com.vinylshop.exception.ResourceNotFoundException;
import com.vinylshop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public void checkQuantity(Product product, int quantity) throws InsufficientStockException {
        final int vinylQuantity = product.getQuantity();
        if (vinylQuantity == 0) {
            throw new InsufficientStockException(
                "Item is out of stock and cannot be added to the cart.",
                product.getId(), "Product", 0);
        } else if (vinylQuantity < quantity) {
            throw new InsufficientStockException(
                "Cannot add more items. Only " + product.getQuantity() + " units available in stock.",
                product.getId(), "Product", product.getQuantity());
        }
    }

}
