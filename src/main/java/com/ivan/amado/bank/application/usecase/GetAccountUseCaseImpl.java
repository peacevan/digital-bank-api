package com.ivan.amado.bank.application.usecase;

import com.ivan.amado.bank.application.dto.AccountResponse;
import com.ivan.amado.bank.application.port.input.GetAccountUseCase;
import com.ivan.amado.bank.application.port.output.AccountRepository;
import com.ivan.amado.bank.domain.model.Account;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GetAccountUseCaseImpl implements GetAccountUseCase {

    private final AccountRepository repository;

    public GetAccountUseCaseImpl(AccountRepository repository) {
        this.repository = repository;
    }

    @Override
    public AccountResponse getById(UUID id) {
        Account account = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("account not found"));
        return new AccountResponse(account.getId(), account.getName(), account.getBalance());
    }
}
