package com.ivan.amado.bank.infrastructure.persistence.repository;

import com.ivan.amado.bank.infrastructure.persistence.entity.TransferEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TransferRepository extends JpaRepository<TransferEntity, UUID> {
    List<TransferEntity> findByFromAccountIdOrToAccountIdOrderByCreatedAtDesc(UUID fromAccountId, UUID toAccountId);
}
