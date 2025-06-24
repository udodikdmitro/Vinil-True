package com.vinylshop.repository;

import com.vinylshop.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {

    boolean existsByNameEnAndNameUk(String nameEn, String nameUk);

    Optional<Genre> findByNameEnOrNameUk(String nameEn, String nameUk);

}
