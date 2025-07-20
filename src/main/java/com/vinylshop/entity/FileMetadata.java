package com.vinylshop.entity;

import com.vinylshop.entity.base.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
    name = "file_metadatas",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_file_metadatas_hash", columnNames = "hash")
    }
)
public class FileMetadata extends AuditableEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @Column(nullable = false, length = 40)
    private String hash;

    @OneToOne(
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true,
            optional = false
    )
    @MapsId
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "id", foreignKey = @ForeignKey(name = "fk_file_metadatas_file_data"))
    private FileData fileData;

}
