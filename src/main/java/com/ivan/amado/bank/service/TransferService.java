package com.ivan.amado.bank.service;

import com.ivan.amado.bank.entity.AccountEntity;
import com.ivan.amado.bank.entity.TransferEntity;
import com.ivan.amado.bank.exception.AccountNotFoundException;
import com.ivan.amado.bank.exception.InsufficientFundsException;
import com.ivan.amado.bank.exception.InvalidTransferException;
import com.ivan.amado.bank.repository.AccountRepository;
import com.ivan.amado.bank.repository.TransferRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Service
public class TransferService {

    private static final Logger log = LoggerFactory.getLogger(TransferService.class);

    private final AccountRepository accountRepository;
    private final TransferRepository transferRepository;

    public TransferService(AccountRepository accountRepository, TransferRepository transferRepository) {
        this.accountRepository = accountRepository;
        this.transferRepository = transferRepository;
    }

    @Transactional
    public void transfer(UUID fromId, UUID toId, BigDecimal amount) {
        Objects.requireNonNull(fromId, "fromId is required");
        Objects.requireNonNull(toId, "toId is required");
        Objects.requireNonNull(amount, "amount is required");

        if (fromId.equals(toId)) {
            throw new InvalidTransferException("Cannot transfer to the same account");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransferException("Amount must be positive");
        }

        AccountEntity from = accountRepository.findById(fromId)
                .orElseThrow(() -> new AccountNotFoundException(fromId));
        AccountEntity to = accountRepository.findById(toId)
                .orElseThrow(() -> new AccountNotFoundException(toId));

        if (from.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException(fromId, from.getBalance(), amount);
        }

        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));

        accountRepository.save(from);
        accountRepository.save(to);

        transferRepository.save(new TransferEntity(UUID.randomUUID(), fromId, toId, amount, Instant.now()));

        log.info("[NOTIFICATION] Transfer of {} from accountId={} to accountId={} completed",
                amount.toPlainString(), fromId, toId);
    }
}
