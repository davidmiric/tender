package com.example.tender.repository;

import com.example.tender.entity.Issuer;
import com.example.tender.entity.Tender;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TenderRepository extends JpaRepository<Tender, Long> {

    List<Tender> findAllByIssuer(@Nullable Issuer issuer);
}
