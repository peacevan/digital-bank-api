package com.ivan.amado.bank.controller;

import com.ivan.amado.bank.dto.TransferRecord;
import com.ivan.amado.bank.dto.TransferRequest;
import com.ivan.amado.bank.dto.TransferResponse;
import com.ivan.amado.bank.entity.IdempotencyEntity;
import com.ivan.amado.bank.entity.TransferEntity;
import com.ivan.amado.bank.repository.IdempotencyRepository;
import com.ivan.amado.bank.repository.TransferRepository;
import com.ivan.amado.bank.service.TransferService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/transfers")
public class TransferController {

    private final TransferService transferService;
    private final TransferRepository transferRepository;
    private final IdempotencyRepository idempotencyRepository;

    public TransferController(TransferService transferService,
                              TransferRepository transferRepository,
                              IdempotencyRepository idempotencyRepository) {
        this.transferService = transferService;
        this.transferRepository = transferRepository;
        this.idempotencyRepository = idempotencyRepository;
    }

    @PostMapping
    public ResponseEntity<TransferResponse> transfer(
            @Valid @RequestBody TransferRequest request,
            HttpServletRequest httpRequest) {

        String idempotencyKey = httpRequest.getHeader("Idempotency-Key");

        if (idempotencyKey != null) {
            var cached = idempotencyRepository.findById(idempotencyKey);
            if (cached.isPresent()) {
                IdempotencyEntity entry = cached.get();
                String msg = entry.getMessage() != null ? entry.getMessage() : "Transfer completed";
                if ("OK".equals(entry.getStatus())) {
                    return ResponseEntity.ok(new TransferResponse("OK", msg));
                } else {
                    return ResponseEntity.badRequest().body(new TransferResponse("ERROR", msg));
                }
            }
        }

        try {
            transferService.transfer(request.getFromAccountId(), request.getToAccountId(), request.getAmount());
        } catch (RuntimeException ex) {
            if (idempotencyKey != null) {
                idempotencyRepository.save(new IdempotencyEntity(idempotencyKey, "ERROR", ex.getMessage(), Instant.now()));
            }
            throw ex;
        }

        if (idempotencyKey != null) {
            idempotencyRepository.save(new IdempotencyEntity(idempotencyKey, "OK", "Transfer completed", Instant.now()));
        }

        return ResponseEntity.ok(new TransferResponse("OK", "Transfer completed"));
    }

    @GetMapping("/account/{id}")
    public ResponseEntity<Page<TransferRecord>> getTransfersByAccount(
            @PathVariable UUID id,
            @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        Page<TransferRecord> dto = transferRepository
                .findByAccount(id, pageable)
                .map(r -> new TransferRecord(r.getId(), r.getFromAccountId(), r.getToAccountId(),
                        r.getAmount(), r.getCreatedAt(),
                        r.getFromAccountId().equals(id) ? "SAIDA" : "ENTRADA"));
        return ResponseEntity.ok(dto);
    }
}
