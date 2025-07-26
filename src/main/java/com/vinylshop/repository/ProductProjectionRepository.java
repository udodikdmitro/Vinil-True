package com.vinylshop.repository;

import com.vinylshop.dto.ProductDto;
import com.vinylshop.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface ProductProjectionRepository {

    Page<ProductDto> findAllProjected(Specification<Product> spec, Pageable pageable);

}
