package com.vinylshop.entity;

import com.vinylshop.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
    name = "cart_items",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_cart_items_cart_id_product_id", columnNames = {"cart_id", "product_id"})
    }
)
public class CartItem extends BaseEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(
            fetch = FetchType.LAZY,
            cascade = {CascadeType.REFRESH, CascadeType.MERGE}
    )
    @JoinColumn(name = "cart_id", foreignKey = @ForeignKey(name = "fk_cart_items_carts_id"))
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "product_id", foreignKey = @ForeignKey(name = "fk_cart_items_product_id"))
    private Product product;

    @Column(nullable = false)
    private int quantity;

}
