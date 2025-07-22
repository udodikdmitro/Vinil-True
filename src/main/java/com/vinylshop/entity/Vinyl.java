// VinylRecord.java - сутність для платівок
package com.vinylshop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Сутність, яка представляє вінілову платівку
 */
@Entity
@Table(name = "vinyl")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Vinyl extends Product {

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

    @Column(columnDefinition = "TEXT")
    private String note;

    @ManyToOne
    @JoinColumn(name = "genre_id", foreignKey = @ForeignKey(name = "fk_vinyls_genres_id"))
    private Genre genre;

    @Enumerated(EnumType.STRING)
    private ReleaseType releaseType;

    @Column
    private String format;

    @Column
    private String color;

    // діаметр платівки в дюймах
    @Column
    private Double vinylSize;

    @Column
    private String limitedEditionNumber;

    @Column
    private Boolean isEmbossing;

    @Column
    private Integer totalPressing;

    // швидкість обертання RPM
    @Column
    private Double speed;

    @Column
    private Integer discCount;

    @Column
    private Long externalAlbumId;

}
