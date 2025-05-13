// VinylRecord.java - сутність для платівок
package com.vinylshop.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
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
    private List<FileMetadata> images = new ArrayList<>();

}
