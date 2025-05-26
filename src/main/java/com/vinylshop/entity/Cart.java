package com.vinylshop.entity;

import com.vinylshop.entity.base.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
    name = "carts",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_carts_user_id", columnNames = "user_id")
    }
)
public class Cart extends AuditableEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(
        fetch = FetchType.LAZY,
        optional = false
    )
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_carts_users_id"))
    private User user;

    @OneToMany(
        mappedBy = "cart",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    private List<CartItem> items = new ArrayList<>();

    @Column(nullable = false, length = 3)
    private Currency currency;

}
