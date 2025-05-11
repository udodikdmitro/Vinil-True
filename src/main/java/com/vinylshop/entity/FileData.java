package com.vinylshop.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "file_data")
public class FileData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "bytes", columnDefinition = "bytea not null")
    private byte[] bytes;

    @OneToOne(
            mappedBy = "fileData",
            cascade = {
                CascadeType.DETACH, CascadeType.MERGE,
                CascadeType.REMOVE, CascadeType.REFRESH
            },
            optional = false,
            orphanRemoval = true
    )
    private FileMetadata metadata;

}
