package com.vinylshop.repository;

import com.vinylshop.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long>, JpaSpecificationExecutor<Review> {

    boolean existsByProductIdAndUserEmail(Long productId, String userEmail);
    Optional<Review> findByProductIdAndUserId(Long productId, Long userId);

    Page<Review> findAllByProductId(Long productId, Pageable pageable);

}
