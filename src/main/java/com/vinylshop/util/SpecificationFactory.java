package com.vinylshop.util;

import com.vinylshop.dto.filter.ReviewFilter;
import com.vinylshop.entity.Product;
import com.vinylshop.entity.Review;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class SpecificationFactory {
    private SpecificationFactory() {}

    public static Specification<Review> create(ReviewFilter filter) {
        return (root, query, cb) -> {
            if (filter == null) {
                return null;
            }
            final List<Predicate> predicates = new ArrayList<>();

            if (filter.productId() != null) {
                Join<Review, Product> productJoin = root.join("product", JoinType.INNER);
                Predicate predicate = cb.equal(productJoin.get("id"), filter.productId());
                predicates.add(predicate);
            }

            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }

}
