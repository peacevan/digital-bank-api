package com.ivan.amado.bank.infrastructure.controller;

import com.ivan.amado.bank.application.dto.TransferRequest;
import com.ivan.amado.bank.application.dto.TransferResponse;
import com.ivan.amado.bank.application.port.input.TransferUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transfers")
public class TransferController {

    private final TransferUseCase transferUseCase;

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
}
