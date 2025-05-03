// VinylRecord.java - сутність для платівок
package com.vinylshop.entity;

import jakarta.persistence.*;
import lombok.*;

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

    /** Зображення платівки у форматі byte[] */
    @Lob
    @Column(columnDefinition = "bytea")
    private byte[] image;
}
