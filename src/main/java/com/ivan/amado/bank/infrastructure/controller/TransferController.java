package com.ivan.amado.bank.infrastructure.controller;

import com.ivan.amado.bank.application.dto.TransferRecord;
import com.ivan.amado.bank.application.dto.TransferRequest;
import com.ivan.amado.bank.application.dto.TransferResponse;
import com.ivan.amado.bank.application.port.input.TransferUseCase;
import com.ivan.amado.bank.infrastructure.persistence.entity.TransferEntity;
import com.ivan.amado.bank.infrastructure.persistence.repository.TransferRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/transfers")
public class TransferController {

    private final TransferUseCase transferUseCase;

    @org.springframework.beans.factory.annotation.Autowired(required = false)
    private TransferRepository transferRepository;

    public TransferController(TransferUseCase transferUseCase) {
        this.transferUseCase = transferUseCase;
    }

    @PostMapping
    public ResponseEntity<TransferResponse> transfer(@Valid @RequestBody TransferRequest request) {
        try {
            transferUseCase.transfer(request.getFromAccountId(), request.getToAccountId(), request.getAmount());
            return ResponseEntity.ok(new TransferResponse("OK", "Transfer completed"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new TransferResponse("ERROR", e.getMessage()));
        }
    }

    @GetMapping("/account/{id}")
    public ResponseEntity<List<TransferRecord>> getTransfersByAccount(@PathVariable UUID id) {
        if (transferRepository == null) {
            return ResponseEntity.ok(List.of());
        }
        List<TransferEntity> records = transferRepository.findByFromAccountIdOrToAccountIdOrderByCreatedAtDesc(id, id);
        List<TransferRecord> dto = records.stream()
            .map(r -> new TransferRecord(r.getId(), r.getFromAccountId(), r.getToAccountId(), r.getAmount(), r.getCreatedAt()))
            .collect(Collectors.toList());
        return ResponseEntity.ok(dto);
    }
}
