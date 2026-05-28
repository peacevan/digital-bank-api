package com.ivan.amado.bank.application.usecase;

import com.ivan.amado.bank.application.dto.AccountCreateRequest;
import com.ivan.amado.bank.application.dto.AccountResponse;
import com.ivan.amado.bank.application.port.input.CreateAccountUseCase;
import com.ivan.amado.bank.application.port.output.AccountRepository;
import com.ivan.amado.bank.domain.model.Account;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CreateAccountUseCaseImpl implements CreateAccountUseCase {

    private final AccountRepository repository;

    public CreateAccountUseCaseImpl(AccountRepository repository) {
        this.repository = repository;
    }

    @Override
    public AccountResponse create(AccountCreateRequest request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new IllegalArgumentException("name is required");
        }
        if (request.getInitialBalance() == null) {
            throw new IllegalArgumentException("initialBalance is required");
        }
        if (request.getInitialBalance().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("initialBalance must be >= 0");
        }

        Account account = Account.of(request.getName(), request.getInitialBalance());
        Account saved = repository.save(account);
        return new AccountResponse(saved.getId(), saved.getName(), saved.getBalance());
    }
}
