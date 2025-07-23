package com.vinylshop.util;

import com.vinylshop.dto.filter.*;
import com.vinylshop.entity.Genre;
import com.vinylshop.entity.Product;
import com.vinylshop.entity.Review;
import com.vinylshop.entity.Vinyl;
import jakarta.persistence.criteria.*;
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

    public static Specification<Vinyl> create(VinylFilter filter) {
        return (root, query, cb) -> {
            if (filter == null) {
                return null;
            }
            final List<Predicate> predicates = new ArrayList<>();

            buildProductPredicates(filter, predicates, root, cb);
            buildVinylPredicates(filter, predicates, root, cb);

            if (filter.onSale() != null && filter.onSale()) {
                predicates.add(cb.greaterThan(root.get("discountValue"), 0));
            }

            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }

    public static Specification<Product> createForProduct(ProductFilter filter) {
        return (root, query, cb) -> {
            if (filter == null) {
                return null;
            }
            final List<Predicate> predicates = new ArrayList<>();

            buildProductPredicates(filter, predicates, root, cb);
            buildConcreteProductPredicates(filter, predicates, root, cb);

            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }

    private static void buildConcreteProductPredicates(ProductFilter filter, List<Predicate> predicates,
                                                       Root<?> root, CriteriaBuilder cb) {
        switch (filter.type()) {
            case VINYL ->
                buildVinylPredicates((VinylFilter) filter, predicates, root, cb);
            case GIFT_CERTIFICATE ->
                buildGiftCertificatePredicates((GiftCertificateFilter) filter, predicates, root, cb);
        }
    }

    private static void buildVinylPredicates(VinylFilter filter, List<Predicate> predicates,
                                             Root<?> root, CriteriaBuilder cb) {
        if (filter.search() != null && !filter.search().isBlank()) {
            String text = "%" + filter.search().trim().toLowerCase() + "%";
            List<Predicate> searchPredicates = new ArrayList<>();

            searchPredicates.add(cb.like(cb.lower(root.get("title")), text));
            searchPredicates.add(cb.like(cb.lower(root.get("subtitle")), text));
            searchPredicates.add(cb.like(cb.lower(root.get("artist")), text));
            searchPredicates.add(cb.like(cb.lower(root.get("album")), text));
            searchPredicates.add(cb.like(cb.lower(root.get("label")), text));
            searchPredicates.add(cb.like(cb.lower(root.get("catalogCode")), text));
            searchPredicates.add(cb.like(cb.lower(root.get("note")), text));

            try {
                Join<?, ?> genreJoin = root.join("genre", JoinType.LEFT);
                searchPredicates.add(cb.like(cb.lower(genreJoin.get("nameEn")), text));
                searchPredicates.add(cb.like(cb.lower(genreJoin.get("nameUk")), text));
            } catch (IllegalArgumentException ignored) {}

            predicates.add(cb.or(searchPredicates.toArray(Predicate[]::new)));
        }

        if (filter.genreId() != null) {
            Join<Vinyl, Genre> genreJoin = root.join("genre", JoinType.INNER);
            Predicate predicate = cb.equal(genreJoin.get("id"), filter.genreId());
            predicates.add(predicate);
        }

        if (filter.artist() != null) {
            String artist = filter.artist().toLowerCase();
            Predicate predicate = cb.like(cb.lower(root.get("artist")), '%' + artist + '%');
            predicates.add(predicate);
        }

        if (filter.album() != null) {
            String album = filter.album().toLowerCase();
            Predicate predicate = cb.like(cb.lower(root.get("album")), '%' + album + '%');
            predicates.add(predicate);
        }

        if (filter.yearFrom() != null && filter.yearTo() != null) {
            predicates.add(cb.between(root.get("year"), filter.yearFrom(), filter.yearTo()));
        } else if (filter.yearFrom() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("year"), filter.yearFrom()));
        } else if (filter.yearTo() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("year"), filter.yearTo()));
        }

        if (filter.releaseTypes() != null && !filter.releaseTypes().isEmpty()) {
            Path<?> releaseTypePath = root.get("releaseType");
            Predicate[] condition = filter.releaseTypes().stream()
                .map(releaseType -> cb.equal(releaseTypePath, releaseType))
                .toArray(Predicate[]::new);
            predicates.add(cb.or(condition));
        }
    }

    private static void buildGiftCertificatePredicates(GiftCertificateFilter filter, List<Predicate> predicates,
                                                       Root<?> root, CriteriaBuilder cb) {
    }

    private static void buildProductPredicates(ProductFilter filter, List<Predicate> predicates,
                                               Root<?> root, CriteriaBuilder cb) {
        if (filter.type() != null) {
            predicates.add(cb.equal(root.get("dtype"), filter.type()));
        }

        if (filter.priceTo() != null && filter.priceFrom() != null) {
            predicates.add(cb.between(root.get("price"), filter.priceFrom(), filter.priceTo()));
        } else if (filter.priceFrom() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("price"), filter.priceFrom()));
        } else if (filter.priceTo() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("price"), filter.priceTo()));
        }
    }

}
