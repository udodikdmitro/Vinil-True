package com.vinylshop.repository;

import com.vinylshop.entity.CartItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    @Query("SELECT ci FROM CartItem ci JOIN FETCH ci.product p WHERE p.id = :productId AND ci.cart.user.email = :userEmail")
    Optional<CartItem> findByProductIdAndUserEmail(Long productId, String userEmail);

    Stream<CartItem> findAllByCartId(Long id);

    Page<CartItem> findAllByCartUserEmail(String email, Pageable pageable);

    @Query("SELECT SUM(p.quantity * p.price) FROM CartItem ci JOIN ci.product p WHERE ci.cart.id = :cartId")
    BigDecimal calculateTotalPriceByCartId(Long cartId);

    @Modifying
    void deleteByCartUserEmail(String email);

    @Modifying
    void deleteByCartUserEmailAndProductIdIn(String email, Iterable<Long> productIds);

}
