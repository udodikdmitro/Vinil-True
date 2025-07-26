package com.vinylshop.repository;

import com.vinylshop.dto.ProductDto;
import com.vinylshop.entity.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductProjectionRepositoryImpl implements ProductProjectionRepository {

    private final EntityManager entityManager;

    @Override
    public Page<ProductDto> findAllProjected(Specification<Product> spec, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<ProductDto> query = cb.createQuery(ProductDto.class);
        Root<Product> root = query.from(Product.class);

        Predicate predicate = buildSafePredicate(spec, root, query, cb);

        query.select(cb.construct(
            ProductDto.class,
            root.get("id"),
            root.get("title"),
            root.get("subtitle"),
            root.get("price"),
            root.get("currency"),
            root.get("quantity"),
            root.get("mainImageUrl"),
            root.get("createdAt"),
            root.get("updatedAt"),
            root.get("dtype"),
            root.get("viewsCount")
        )).where(predicate);

        query.orderBy(toOrders(pageable.getSort(), root, cb));

        TypedQuery<ProductDto> typedQuery = entityManager.createQuery(query)
            .setFirstResult((int) pageable.getOffset())
            .setMaxResults(pageable.getPageSize());

        List<ProductDto> content = typedQuery.getResultList();

        long total;
        if (content.size() < pageable.getPageSize() && pageable.getPageNumber() == 0) {
            total = content.size();
        } else if (content.size() < pageable.getPageSize()) {
            total = pageable.getOffset() + content.size();
        } else {
            CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
            Root<Product> countRoot = countQuery.from(Product.class);
            Predicate countPredicate = buildSafePredicate(spec, countRoot, countQuery, cb);
            countQuery.select(cb.count(countRoot)).where(countPredicate);
            total = entityManager.createQuery(countQuery).getSingleResult();
        }

        return new PageImpl<>(content, pageable, total);
    }

    private Predicate buildSafePredicate(Specification<Product> spec, Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        if (spec == null) return cb.conjunction();
        Predicate predicate = spec.toPredicate(root, query, cb);
        return predicate != null ? predicate : cb.conjunction();
    }

    private List<Order> toOrders(Sort sort, Root<Product> root, CriteriaBuilder cb) {
        List<Order> orders = new ArrayList<>();
        for (Sort.Order s : sort) {
            Path<?> path = root.get(s.getProperty());
            orders.add(s.isAscending() ? cb.asc(path) : cb.desc(path));
        }
        return orders;
    }

}