package com.vinylshop.controller;

import com.vinylshop.dto.ReviewCreateRequest;
import com.vinylshop.dto.ReviewDto;
import com.vinylshop.dto.ReviewUpdateRequest;
import com.vinylshop.dto.filter.ReviewFilter;
import com.vinylshop.entity.Review;
import com.vinylshop.mapper.ReviewMapper;
import com.vinylshop.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final ReviewMapper reviewMapper;

    @GetMapping("/v1/products/reviews")
    public ResponseEntity<Page<ReviewDto>> getAllReviews(
        @ModelAttribute ReviewFilter filter,
        @PageableDefault(page = 0, size = 10, sort = "createdAt") Pageable pageable
    ) {
        return ResponseEntity.ok(reviewService.getAll(filter, pageable)
            .map(reviewMapper::toDto));
    }

    @GetMapping("/v1/products/reviews/{id}")
    public ResponseEntity<ReviewDto> getReviewById(
        @PathVariable Long id
    ) {
        return ResponseEntity.of(reviewService.getById(id)
            .map(reviewMapper::toDto));
    }

    @PostMapping("/v1/products/reviews")
    public ResponseEntity<ReviewDto> createProductReview(
        @RequestBody ReviewCreateRequest requestBody,
        Authentication authentication
    ) {
        String email = authentication.getName();
        ReviewDto created = reviewService.createReviewFromDto(email, requestBody);
        return ResponseEntity.ok(created);
    }

    @PatchMapping("/v1/products/reviews/{id}")
    public ResponseEntity<ReviewDto> updateReviewById(
        @PathVariable Long id,
        @RequestBody ReviewUpdateRequest requestBody,
        Authentication authentication
    ) {
        Review updated = reviewService.updateById(id, requestBody, authentication.getName());
        return ResponseEntity.ok(reviewMapper.toDto(updated));
    }

    @DeleteMapping("/v1/products/reviews/{id}")
    public ResponseEntity<?> deleteReviewById(
        @PathVariable Long id,
        Authentication authentication
    ) {
        reviewService.deleteById(id, authentication.getName());
        return ResponseEntity.ok().body("Відгук видалено");
    }

}
