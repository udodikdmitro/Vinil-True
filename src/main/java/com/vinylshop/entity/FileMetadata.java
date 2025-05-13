package com.vinylshop.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "file_metadatas")
public class FileMetadata extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String originalName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String contentType;

    private String url;

    private String contentUrl;

    @Column(nullable = false)
    private long size;

    @OneToOne(
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true,
            optional = false
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "file_data_id", foreignKey = @ForeignKey(name = "fk_file_metadatas_file_data"))
    private FileData fileData;

}
