package com.ivan.amado.bank.service;

import com.ivan.amado.bank.dto.AccountCreateRequest;
import com.ivan.amado.bank.dto.AccountResponse;
import com.ivan.amado.bank.entity.AccountEntity;
import com.ivan.amado.bank.exception.AccountNotFoundException;
import com.ivan.amado.bank.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public AccountResponse create(AccountCreateRequest request) {
        AccountEntity entity = new AccountEntity();
        entity.setId(UUID.randomUUID());
        entity.setOwnerName(request.getName());
        entity.setBalance(request.getInitialBalance());
        AccountEntity saved = accountRepository.save(entity);
        return toResponse(saved);
    }

    public AccountResponse getById(UUID id) {
        AccountEntity entity = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));
        return toResponse(entity);
    }

    private AccountResponse toResponse(AccountEntity entity) {
        return new AccountResponse(entity.getId(), entity.getOwnerName(), entity.getBalance());
    }
}
