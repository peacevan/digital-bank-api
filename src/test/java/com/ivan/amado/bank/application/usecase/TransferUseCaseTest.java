package com.ivan.amado.bank.application.usecase;

import com.ivan.amado.bank.application.port.input.TransferUseCase;
import com.ivan.amado.bank.infrastructure.persistence.adapter.InMemoryAccountRepository;
import com.ivan.amado.bank.domain.model.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class TransferUseCaseTest {

    private InMemoryAccountRepository repository;
    private TransferUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = new InMemoryAccountRepository();
        useCase = new TransferUseCaseImpl(repository);
    }

    @Test
    void should_transfer_amount_between_accounts() {
        Account a = Account.of("A", new BigDecimal("100.00"));
        Account b = Account.of("B", new BigDecimal("50.00"));
        repository.save(a);
        repository.save(b);

        useCase.transfer(a.getId(), b.getId(), new BigDecimal("30.00"));

        assertEquals(new BigDecimal("70.00"), a.getBalance());
        assertEquals(new BigDecimal("80.00"), b.getBalance());
    }

    @Test
    void should_not_allow_transfer_when_insufficient_funds() {
        Account a = Account.of("A", new BigDecimal("20.00"));
        Account b = Account.of("B", new BigDecimal("50.00"));
        repository.save(a);
        repository.save(b);

        assertThrows(IllegalArgumentException.class,
                () -> useCase.transfer(a.getId(), b.getId(), new BigDecimal("30.00")));
    }

    @Test
    void should_not_allow_transfer_to_same_account() {
        Account a = Account.of("A", new BigDecimal("100.00"));
        repository.save(a);

        assertThrows(IllegalArgumentException.class,
                () -> useCase.transfer(a.getId(), a.getId(), new BigDecimal("10.00")));
    }
}
