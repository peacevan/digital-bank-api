package com.ivan.amado.bank.repository;

import com.ivan.amado.bank.entity.TransferEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TransferRepository extends JpaRepository<TransferEntity, UUID> {
    List<TransferEntity> findByFromAccountIdOrToAccountIdOrderByCreatedAtDesc(UUID fromAccountId, UUID toAccountId);
}
