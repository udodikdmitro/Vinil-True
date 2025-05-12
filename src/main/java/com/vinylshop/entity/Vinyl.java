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
            name = "files_references",
            joinColumns = @JoinColumn(name = "vinyl_id", foreignKey = @ForeignKey(name = "fk_files_references_vinyl")),
            inverseJoinColumns = @JoinColumn(name = "file_metadata_id", foreignKey = @ForeignKey(name = "fk_files_references_file_metadatas"))
    )
    private List<FileMetadata> images = new ArrayList<>();

}
