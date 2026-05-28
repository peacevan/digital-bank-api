package com.ivan.bank.application.usecase;

import com.ivan.bank.application.dto.AccountCreateRequest;
import com.ivan.bank.application.dto.AccountResponse;
import com.ivan.bank.infrastructure.persistence.adapter.InMemoryAccountRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class CreateAccountUseCaseTest {

    private CreateAccountUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new CreateAccountUseCaseImpl(new InMemoryAccountRepository());
    }

    @Test
    void create_validAccount_returnsResponse() {
        AccountCreateRequest req = new AccountCreateRequest("Alice", new BigDecimal("100.00"));
        AccountResponse resp = useCase.create(req);
        Assertions.assertNotNull(resp.getId());
        Assertions.assertEquals("Alice", resp.getName());
        Assertions.assertEquals(new BigDecimal("100.00"), resp.getBalance());
    }

    @Test
    void create_blankName_throws() {
        AccountCreateRequest req = new AccountCreateRequest(" ", new BigDecimal("10"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> useCase.create(req));
    }

    @Test
    void create_negativeBalance_throws() {
        AccountCreateRequest req = new AccountCreateRequest("Bob", new BigDecimal("-1"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> useCase.create(req));
    }
}
