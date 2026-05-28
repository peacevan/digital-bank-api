package com.ivan.bank.infrastructure.persistence.adapter;

import com.ivan.bank.application.port.output.AccountRepository;
import com.ivan.bank.domain.model.Account;
import com.ivan.bank.infrastructure.persistence.entity.AccountEntity;
import com.ivan.bank.infrastructure.persistence.repository.SpringDataAccountRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JpaAccountAdapter implements AccountRepository {

    private final SpringDataAccountRepository repository;

    public JpaAccountAdapter(SpringDataAccountRepository repository) {
        this.repository = repository;
    }

    @Override
    public Account save(Account account) {
        AccountEntity entity = toEntity(account);
        AccountEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Account> findById(java.util.UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    private AccountEntity toEntity(Account a) {
        AccountEntity e = new AccountEntity();
        e.setId(a.getId());
        e.setName(a.getName());
        e.setBalance(a.getBalance());
        return e;
    }

    private Account toDomain(AccountEntity e) {
        Account a = new Account(e.getId(), e.getName(), e.getBalance());
        return a;
    }
}
