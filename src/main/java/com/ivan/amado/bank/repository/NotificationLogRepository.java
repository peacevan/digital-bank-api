package com.ivan.amado.bank.repository;

import com.ivan.amado.bank.entity.NotificationLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NotificationLogRepository extends JpaRepository<NotificationLogEntity, UUID> {
}
