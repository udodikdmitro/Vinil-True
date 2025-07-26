package com.vinylshop.repository;

import com.vinylshop.entity.FileReference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface FileReferenceRepository extends JpaRepository<FileReference, Long> {

    long countByFileId(Long fileId);
    void deleteByProductIdAndFileIdIn(Long productId, Collection<Long> fileIds);

}
