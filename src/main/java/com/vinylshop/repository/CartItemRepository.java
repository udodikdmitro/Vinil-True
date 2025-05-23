package com.vinylshop.repository;

import com.vinylshop.entity.CartItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    @Query("SELECT ci FROM CartItem ci WHERE ci.vinyl.id = :vinylId AND ci.cart.user.email = :userEmail")
    Optional<CartItem> findByVinylIdAndUserEmail(@Param("vinylId") Long vinylId, @Param("userEmail") String userEmail);

    Page<CartItem> findAllByCartUserEmail(String email, Pageable pageable);

    @Query("SELECT SUM(ci.quantity * v.price) FROM CartItem ci JOIN ci.vinyl v WHERE ci.cart.id = :cartId")
    BigDecimal calculateTotalPriceByCartId(@Param("cartId") Long cartId);

    @Modifying
    void deleteByCartUserEmail(String email);

    @Modifying
    void deleteByCartUserEmailAndVinylIdIn(String email, Iterable<Long> vinylIds);

}
