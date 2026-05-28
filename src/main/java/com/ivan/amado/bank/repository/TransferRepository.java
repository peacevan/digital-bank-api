package com.ivan.amado.bank.repository;

import com.ivan.amado.bank.entity.TransferEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface TransferRepository extends JpaRepository<TransferEntity, UUID> {

    @Query("SELECT t FROM TransferEntity t WHERE t.fromAccountId = :id OR t.toAccountId = :id ORDER BY t.createdAt DESC")
    Page<TransferEntity> findByAccount(@Param("id") UUID id, Pageable pageable);
}
