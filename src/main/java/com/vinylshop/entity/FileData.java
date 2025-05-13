package com.vinylshop.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "file_data")
public class FileData extends BaseEntity {

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
