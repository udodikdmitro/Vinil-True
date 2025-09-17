package com.vinylshop.service;

import com.vinylshop.dto.ReviewCreateRequest;
import com.vinylshop.dto.ReviewDto;
import com.vinylshop.dto.ReviewUpdateRequest;
import com.vinylshop.dto.filter.ReviewFilter;
import com.vinylshop.entity.Product;
import com.vinylshop.entity.Review;
import com.vinylshop.entity.Role;
import com.vinylshop.entity.User;
import com.vinylshop.exception.ResourceAlreadyExistException;
import com.vinylshop.exception.ResourceNotFoundException;
import com.vinylshop.mapper.ReviewMapper;
import com.vinylshop.repository.ReviewRepository;
import com.vinylshop.repository.UserRepository;
import com.vinylshop.util.SpecificationFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductService productService;
    private final UserRepository userRepository;
    private final ReviewMapper reviewMapper;

    @Transactional
    public ReviewDto createReviewFromDto(String email, ReviewCreateRequest request)
        throws ResourceNotFoundException, ResourceAlreadyExistException {
        Review review = reviewMapper.toEntity(request);
        Review created = create(review, email, request.productId());
        return reviewMapper.toDto(created);
    }

    @Transactional
    public Review create(Review review, String email, Long productId) {
        preProcessing(review, email, productId);
        return reviewRepository.save(review);
    }

    @Transactional(readOnly = true)
    public Page<Review> getAll(ReviewFilter filter, Pageable pageable) {
        Specification<Review> specification = SpecificationFactory.create(filter);
        return reviewRepository.findAll(specification, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Review> getAllByProductId(Long productId, Pageable pageable) {
        return reviewRepository.findAllByProductId(productId, pageable);
    }

    @Transactional(readOnly = true)
    public Optional<Review> getById(Long id) {
        return reviewRepository.findById(id);
    }

    @Transactional
    public Review updateById(Long id, ReviewUpdateRequest dto, String email)
        throws AccessDeniedException, ResourceNotFoundException {
        Review review = getByIdOrThrow(id);

        checkWriteAccess(review, email, false);

        if (dto.rating() != null) {
            review.setRating(dto.rating());
        }
        if (dto.comment() != null) {
            review.setComment(dto.comment());
        }
        return reviewRepository.save(review);
    }

    @Transactional
    public void deleteById(Long id, String email)
        throws AccessDeniedException, ResourceNotFoundException {
        Review review = getByIdOrThrow(id);
        checkWriteAccess(review, email, true);
        reviewRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Review getByIdOrThrow(Long id) throws ResourceNotFoundException {
        return getById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Review " + id + " not found", id, "Review"));
    }

    private void preProcessing(Review review, String email, Long productId) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User " + email + " not found", email, "User"));
        review.setUser(user);

        Product product = productService.getByIdOrThrow(productId);
        review.setProduct(product);

        throwIfAlreadyExists(productId, email);
    }

    private void checkWriteAccess(Review review, String email, boolean allowAdminToChange)
        throws AccessDeniedException {
        if (review.getUser().getEmail().equals(email)) {
            return;
        }
        if (allowAdminToChange && review.getUser().getRole().compareTo(Role.ADMIN) != 0) {
            return;
        }
        throw new AccessDeniedException("Access Denied");
    }

    private void throwIfAlreadyExists(Long productId, String userEmail) throws ResourceAlreadyExistException {
        if (reviewRepository.existsByProductIdAndUserEmail(productId, userEmail)) {
            String id = productId + "-" + userEmail;
            throw new ResourceAlreadyExistException("User " + userEmail +
                                                    " already created a review for specify product " +
                                                    productId, id, "Review");
        }
    }

}
