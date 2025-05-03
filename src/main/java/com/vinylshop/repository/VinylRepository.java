// VinylRecordRepository.java - репозиторій
package com.vinylshop.repository;

import com.vinylshop.entity.Vinyl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

/**
 * Репозиторій для управління платівками
 */
@Repository
public interface VinylRepository extends JpaRepository<Vinyl, Long> {
    Collection<Object> findTop10ByOrderByYearDesc();
}
