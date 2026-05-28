package com.ivan.amado.bank.controller;

import com.ivan.amado.bank.entity.NotificationLogEntity;
import com.ivan.amado.bank.repository.NotificationLogRepository;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationLogRepository notificationLogRepository;

    public NotificationController(NotificationLogRepository notificationLogRepository) {
        this.notificationLogRepository = notificationLogRepository;
    }

    @GetMapping
    public ResponseEntity<Page<NotificationLogEntity>> list(
            @ParameterObject @PageableDefault(size = 20, sort = "sentAt") Pageable pageable) {
        return ResponseEntity.ok(notificationLogRepository.findAll(pageable));
    }
}
