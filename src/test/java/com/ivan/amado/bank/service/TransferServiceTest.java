package com.ivan.amado.bank.service;

import com.ivan.amado.bank.entity.AccountEntity;
import com.ivan.amado.bank.exception.AccountNotFoundException;
import com.ivan.amado.bank.exception.InsufficientFundsException;
import com.ivan.amado.bank.exception.InvalidTransferException;
import com.ivan.amado.bank.repository.AccountRepository;
import com.ivan.amado.bank.repository.TransferRepository;
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
import static org.mockito.Mockito.*;

class TransferServiceTest {

    @Mock
    AccountRepository accountRepository;

    @Mock
    TransferRepository transferRepository;

    @InjectMocks
    TransferService transferService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    private AccountEntity entity(UUID id, String ownerName, BigDecimal balance) {
        AccountEntity e = new AccountEntity();
        e.setId(id);
        e.setOwnerName(ownerName);
        e.setBalance(balance);
        e.setVersion(0L);
        return e;
    }

    @Test
    void deve_realizar_transferencia_com_sucesso() {
        UUID fromId = UUID.randomUUID();
        UUID toId   = UUID.randomUUID();
        AccountEntity from = entity(fromId, "Alice", new BigDecimal("1000.00"));
        AccountEntity to   = entity(toId,   "Bob",   new BigDecimal("0.00"));

        when(accountRepository.findById(fromId)).thenReturn(Optional.of(from));
        when(accountRepository.findById(toId)).thenReturn(Optional.of(to));
        when(accountRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(transferRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        transferService.transfer(fromId, toId, new BigDecimal("300.00"));

        assertThat(from.getBalance()).isEqualByComparingTo("700.00");
        assertThat(to.getBalance()).isEqualByComparingTo("300.00");
        verify(accountRepository, times(2)).save(any());
        verify(transferRepository).save(any());
    }

    @Test
    void deve_lancar_excecao_quando_saldo_insuficiente() {
        UUID fromId = UUID.randomUUID();
        UUID toId   = UUID.randomUUID();

        when(accountRepository.findById(fromId)).thenReturn(Optional.of(entity(fromId, "Alice", new BigDecimal("50.00"))));
        when(accountRepository.findById(toId)).thenReturn(Optional.of(entity(toId, "Bob", new BigDecimal("0.00"))));

        assertThatThrownBy(() -> transferService.transfer(fromId, toId, new BigDecimal("200.00")))
                .isInstanceOf(InsufficientFundsException.class);
    }

    @Test
    void deve_lancar_excecao_quando_conta_origem_nao_encontrada() {
        UUID fromId = UUID.randomUUID();
        UUID toId   = UUID.randomUUID();

        when(accountRepository.findById(fromId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transferService.transfer(fromId, toId, new BigDecimal("100.00")))
                .isInstanceOf(AccountNotFoundException.class);
    }

    @Test
    void deve_lancar_excecao_quando_conta_destino_nao_encontrada() {
        UUID fromId = UUID.randomUUID();
        UUID toId   = UUID.randomUUID();

        when(accountRepository.findById(fromId)).thenReturn(Optional.of(entity(fromId, "Alice", new BigDecimal("1000.00"))));
        when(accountRepository.findById(toId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transferService.transfer(fromId, toId, new BigDecimal("100.00")))
                .isInstanceOf(AccountNotFoundException.class);
    }

    @Test
    void deve_lancar_excecao_quando_transferencia_para_mesma_conta() {
        UUID id = UUID.randomUUID();

        assertThatThrownBy(() -> transferService.transfer(id, id, new BigDecimal("100.00")))
                .isInstanceOf(InvalidTransferException.class);
    }

    @Test
    void deve_lancar_excecao_quando_valor_zero() {
        UUID fromId = UUID.randomUUID();
        UUID toId   = UUID.randomUUID();

        assertThatThrownBy(() -> transferService.transfer(fromId, toId, BigDecimal.ZERO))
                .isInstanceOf(InvalidTransferException.class);
    }

    @Test
    void deve_lancar_excecao_quando_valor_negativo() {
        UUID fromId = UUID.randomUUID();
        UUID toId   = UUID.randomUUID();

        assertThatThrownBy(() -> transferService.transfer(fromId, toId, new BigDecimal("-10.00")))
                .isInstanceOf(InvalidTransferException.class);
    }
}
