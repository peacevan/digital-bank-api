package com.ivan.amado.bank.repository;

import com.ivan.amado.bank.entity.IdempotencyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdempotencyRepository extends JpaRepository<IdempotencyEntity, String> {
}
