package com.vinylshop.entity;

import com.vinylshop.entity.base.AuditableEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "products")
@Inheritance(strategy = InheritanceType.JOINED)
public class Product extends AuditableEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(precision = 10, scale = 2)
    private BigDecimal discountPrice;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal discountValue = BigDecimal.ZERO;

    @Column(nullable = false)
    private Currency currency;

    @Column(nullable = false)
    private int quantity = 0;

    @OneToMany(
        fetch = FetchType.EAGER,
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    @JoinTable(
        name = "files_references",
        joinColumns = @JoinColumn(name = "product_id", foreignKey = @ForeignKey(name = "fk_files_references_products")),
        inverseJoinColumns = @JoinColumn(name = "file_metadata_id", foreignKey = @ForeignKey(name = "fk_files_references_file_metadatas"))
    )
    private List<FileMetadata> images = new ArrayList<>();

    @OneToMany(
        mappedBy = "product",
        fetch = FetchType.LAZY,
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<Review> reviews = new ArrayList<>();

    public BigDecimal getFinalPrice() {
        return discountPrice == null ? price : discountPrice;
    }

}
