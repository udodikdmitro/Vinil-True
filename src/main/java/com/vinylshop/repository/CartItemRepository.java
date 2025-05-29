package com.vinylshop.repository;

import com.vinylshop.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    @Query("SELECT ci FROM CartItem ci JOIN FETCH ci.product p WHERE p.id = :productId AND ci.cart.user.email = :userEmail")
    Optional<CartItem> findByProductIdAndUserEmail(Long productId, String userEmail);

    @Modifying
    void deleteByCartUserEmail(String email);

    @Modifying
    void deleteByCartUserEmailAndProductIdIn(String email, Iterable<Long> productIds);

}
