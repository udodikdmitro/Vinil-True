package com.vinylshop.entity;

import com.vinylshop.entity.base.AuditableEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
    name = "genres",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_genres_name_en_name_uk", columnNames = {"name_en", "name_uk"})
    }
)
public class Genre extends AuditableEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name_en", nullable = false)
    private String nameEn;

    @Column(name = "name_uk", nullable = false)
    private String nameUk;

}
