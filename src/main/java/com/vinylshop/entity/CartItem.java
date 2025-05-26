package com.vinylshop.entity;

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
                @UniqueConstraint(name = "uq_cart_items_cart_id_vinyl_id", columnNames = {"cart_id", "vinyl_id"})
        }
)
public class CartItem extends BaseEntity {

    @ManyToOne(
            fetch = FetchType.LAZY,
            cascade = {CascadeType.REFRESH, CascadeType.MERGE}
    )
    @JoinColumn(name = "cart_id", foreignKey = @ForeignKey(name = "fk_cart_items_carts_id"))
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "vinyl_id", foreignKey = @ForeignKey(name = "fk_cart_items_vinyls_id"))
    private Vinyl vinyl;

    @Column(nullable = false)
    private int quantity;

}
