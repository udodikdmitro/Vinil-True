package com.vinylshop.repository;

import com.vinylshop.entity.GiftCertificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GiftCertificateRepository extends JpaRepository<GiftCertificate, Long> {

    Optional<GiftCertificate> findByCode(String code);

}
