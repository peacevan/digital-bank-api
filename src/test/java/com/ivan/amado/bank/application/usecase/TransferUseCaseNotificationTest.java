package com.ivan.amado.bank.application.usecase;

import com.ivan.amado.bank.application.port.output.Notifier;
import com.ivan.amado.bank.infrastructure.persistence.adapter.InMemoryAccountRepository;
import com.ivan.amado.bank.domain.model.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class TransferUseCaseNotificationTest {

    private InMemoryAccountRepository repository;
    private Notifier notifier;
    private TransferUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        repository = new InMemoryAccountRepository();
        notifier = mock(Notifier.class);
        useCase = new TransferUseCaseImpl(repository, notifier);
    }

    @Test
    void should_send_notifications_on_successful_transfer() {
        Account a = Account.of("A", new BigDecimal("100.00"));
        Account b = Account.of("B", new BigDecimal("50.00"));
        repository.save(a);
        repository.save(b);

        useCase.transfer(a.getId(), b.getId(), new BigDecimal("30.00"));

        verify(notifier).notify(eq(a.getId()), contains("Transfer sent"));
        verify(notifier).notify(eq(b.getId()), contains("Transfer received"));
    }

    @Test
    void should_not_send_notifications_when_transfer_fails() {
        Account a = Account.of("A", new BigDecimal("10.00"));
        Account b = Account.of("B", new BigDecimal("50.00"));
        repository.save(a);
        repository.save(b);

        assertThrows(IllegalArgumentException.class,
                () -> useCase.transfer(a.getId(), b.getId(), new BigDecimal("20.00")));

        verifyNoInteractions(notifier);
    }
}
