package com.vinylshop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
    name = "reviews",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uq_reviews_user_id_product_id",
            columnNames = {"product_id", "user_id"}
        )
    },
    indexes = {
        @Index(
            name = "idx_reviews_product_id",
            columnList = "product_id"
        )
    }
)
public class Review extends BaseEntity {

    @ManyToOne(fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(
        name = "product_id",
        foreignKey = @ForeignKey(name = "fk_reviews_products_id")
    )
    private Product product;

    @ManyToOne(fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(
        name = "user_id",
        foreignKey = @ForeignKey(name = "fk_reviews_users_id")
    )
    private User user;

    @Column(nullable = false)
    private int rating;

    @Column(columnDefinition = "TEXT")
    private String comment;

}
