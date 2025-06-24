package com.vinylshop.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
public class Genre extends BaseEntity {

    @Column(name = "name_en", nullable = false)
    private String nameEn;

    @Column(name = "name_uk", nullable = false)
    private String nameUk;

}
