package com.ivan.amado.bank.service;

import com.ivan.amado.bank.dto.AccountCreateRequest;
import com.ivan.amado.bank.dto.AccountResponse;
import com.ivan.amado.bank.entity.AccountEntity;
import com.ivan.amado.bank.exception.AccountNotFoundException;
import com.ivan.amado.bank.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class AccountServiceTest {

    @Mock
    AccountRepository accountRepository;

    @InjectMocks
    AccountService accountService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    private AccountEntity entityWith(UUID id, String ownerName, BigDecimal balance) {
        AccountEntity e = new AccountEntity();
        e.setId(id);
        e.setOwnerName(ownerName);
        e.setBalance(balance);
        e.setVersion(0L);
        return e;
    }

    @Test
    void deve_criar_conta_com_sucesso() {
        AccountCreateRequest request = new AccountCreateRequest("Alice", new BigDecimal("500.00"));

        AccountEntity saved = entityWith(UUID.randomUUID(), "Alice", new BigDecimal("500.00"));
        when(accountRepository.save(any(AccountEntity.class))).thenReturn(saved);

        AccountResponse response = accountService.create(request);

        assertThat(response.getId()).isNotNull();
        assertThat(response.getName()).isEqualTo("Alice");
        assertThat(response.getBalance()).isEqualByComparingTo("500.00");
    }

    @Test
    void deve_retornar_conta_por_id() {
        UUID id = UUID.randomUUID();
        AccountEntity entity = entityWith(id, "Bob", new BigDecimal("1000.00"));

        when(accountRepository.findById(id)).thenReturn(Optional.of(entity));

        AccountResponse response = accountService.getById(id);

        assertThat(response.getId()).isEqualTo(id);
        assertThat(response.getName()).isEqualTo("Bob");
        assertThat(response.getBalance()).isEqualByComparingTo("1000.00");
    }

    @Test
    void deve_lancar_excecao_quando_conta_nao_encontrada() {
        UUID id = UUID.randomUUID();
        when(accountRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.getById(id))
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessageContaining(id.toString());
    }
}
