package com.ivan.amado.bank.application.port.output;

import com.ivan.amado.bank.domain.model.Account;

import java.util.Optional;
import java.util.UUID;

public interface AccountRepository {
    Account save(Account account);
    Optional<Account> findById(UUID id);
}
