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
@Table(name = "vinyl")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vinyl extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String album;

    @Column(nullable = false)
    private String artist;

    @Column(nullable = false)
    private int year;

    @Column(nullable = false)
    private String label;

    @Column(nullable = false)
    private String countryOfOrigin;

    @Column(nullable = false)
    private String catalogCode;

    @Column(nullable = false)
    private String condition;

    @Column(nullable = false)
    private String envelopeCondition;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Currency currency;

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

    @Column(columnDefinition = "TEXT")
    private String note;

    @Column(nullable = false)
    private int quantity = 0;

    @ManyToOne
    @JoinColumn(name = "genre_id", foreignKey = @ForeignKey(name = "fk_vinyls_genres_id"))
    private Genre genre;

    @Enumerated(EnumType.STRING)
    private ReleaseType releaseType;

}
