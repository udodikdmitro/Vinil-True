package com.vinylshop.repository;

import com.vinylshop.entity.FileData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FileDataRepository extends JpaRepository<FileData, UUID> {

}
