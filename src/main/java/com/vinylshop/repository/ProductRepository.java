package com.vinylshop.repository;

import com.vinylshop.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Modifying
    @Query("UPDATE Product p SET p.viewsCount = p.viewsCount + 1 WHERE p.id = :productId")
    void incrementViewsCount(Long productId);

}
