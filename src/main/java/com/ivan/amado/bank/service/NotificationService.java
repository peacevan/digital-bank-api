package com.ivan.amado.bank.service;

import com.ivan.amado.bank.entity.NotificationLogEntity;
import com.ivan.amado.bank.repository.NotificationLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final RestTemplate restTemplate;
    private final NotificationLogRepository notificationLogRepository;

    @Value("${notification.url:https://util.devi.tools/api/v1/notify}")
    private String notificationUrl;

    public NotificationService(RestTemplate restTemplate, NotificationLogRepository notificationLogRepository) {
        this.restTemplate = restTemplate;
        this.notificationLogRepository = notificationLogRepository;
    }

    @Async
    public void sendTransferNotification(UUID fromId, UUID toId, BigDecimal amount) {
        String status;
        String errorMessage = null;
        try {
            Map<String, String> payload = Map.of(
                    "fromAccountId", fromId.toString(),
                    "toAccountId",   toId.toString(),
                    "amount",        amount.toPlainString()
            );
            restTemplate.postForObject(notificationUrl, payload, Void.class);
            status = "SENT";
            log.info("[NOTIFICATION] Sent — from={} to={} amount={}", fromId, toId, amount.toPlainString());
        } catch (Exception ex) {
            status = "FAILED";
            errorMessage = ex.getMessage();
            log.warn("[NOTIFICATION] Failed (non-blocking) — {}", ex.getMessage());
        }
        notificationLogRepository.save(new NotificationLogEntity(
                UUID.randomUUID(), fromId, toId, amount, status, errorMessage, Instant.now()
        ));
    }
}
