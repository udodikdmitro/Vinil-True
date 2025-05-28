// VinylRecord.java - сутність для платівок
package com.vinylshop.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

/**
 * Сутність, яка представляє вінілову платівку
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vinyl extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String artist;

    @Column(nullable = false)
    private int year;

    @OneToMany(
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JoinTable(
            name = "files_references",
            joinColumns = @JoinColumn(name = "vinyl_id", foreignKey = @ForeignKey(name = "fk_files_references_vinyl")),
            inverseJoinColumns = @JoinColumn(name = "file_metadata_id", foreignKey = @ForeignKey(name = "fk_files_references_file_metadatas"))
    )
    @Builder.Default
    private List<FileMetadata> images = new ArrayList<>();

    @OneToMany(
            mappedBy = "vinyl",
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @Builder.Default
    private List<CartItem> cartItems = new ArrayList<>();

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Currency currency;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal originalPrice;

    @Column(nullable = false)
    private Currency originalCurrency;

    @Column(nullable = false)
    private int quantity = 0;

}
