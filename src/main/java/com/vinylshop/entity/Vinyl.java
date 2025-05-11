// VinylRecord.java - сутність для платівок
package com.vinylshop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Сутність, яка представляє вінілову платівку
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vinyl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Назва платівки */
    private String title;

    /** Ім'я виконавця */
    private String artist;

    /** Рік випуску */
    private int year;

    @OneToMany(
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JoinTable(
            name = "vinil_images",
            joinColumns = @JoinColumn(name = "vinil_id"),
            inverseJoinColumns = @JoinColumn(name = "file_metadata_id")
    )
    private List<FileMetadata> images = new ArrayList<>();

}
