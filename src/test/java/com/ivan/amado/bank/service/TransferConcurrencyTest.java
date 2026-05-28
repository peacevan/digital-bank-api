package com.ivan.amado.bank.service;

import com.ivan.amado.bank.dto.AccountCreateRequest;
import com.ivan.amado.bank.repository.AccountRepository;
import com.ivan.amado.bank.repository.IdempotencyRepository;
import com.ivan.amado.bank.repository.TransferRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Teste de concorrência para transferências simultâneas entre contas.
 *
 * <p>Valida que o mecanismo de Optimistic Locking (@Version no AccountEntity)
 * combinado com @Transactional no TransferService garante consistência financeira
 * mesmo sob carga concorrente real — sem locks pessimistas ou sincronização manual.</p>
 *
 * <p><b>Estratégia do teste:</b></p>
 * <ol>
 *   <li>Cria duas contas: origem (R$ 1.000) e destino (R$ 0).</li>
 *   <li>Dispara 10 threads simultaneamente, cada uma tentando transferir R$ 100.</li>
 *   <li>Usa {@link CountDownLatch} para garantir disparo simultâneo (máxima contenção).</li>
 *   <li>Colisões de versão lançam {@link org.springframework.orm.ObjectOptimisticLockingFailureException},
 *       que são silenciadas — simulando o comportamento esperado em produção.</li>
 * </ol>
 *
 * <p><b>Invariantes verificadas:</b></p>
 * <ul>
 *   <li>Conservação de valor: {@code saldoOrigem + saldoDestino == R$ 1.000} (dinheiro não é criado nem destruído).</li>
 *   <li>Ao menos 1 transferência deve ter sido confirmada.</li>
 *   <li>Nenhum saldo pode ser negativo (sem double-spend).</li>
 *   <li>Saldo do destino corresponde exatamente a {@code transferênciasConfirmadas × R$ 100}.</li>
 * </ul>
 */
@SpringBootTest
@ActiveProfiles("test")
class TransferConcurrencyTest {

    @Autowired
    TransferService transferService;

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    TransferRepository transferRepository;

    @Autowired
    IdempotencyRepository idempotencyRepository;

    private UUID fromId;
    private UUID toId;

    /**
     * Recria o estado limpo antes de cada execução do teste:
     * apaga todos os registros e cria duas contas frescas.
     */
    @BeforeEach
    void setUp() {
        idempotencyRepository.deleteAll();
        transferRepository.deleteAll();
        accountRepository.deleteAll();

        AccountCreateRequest origin = new AccountCreateRequest();
        origin.setName("Origin");
        origin.setInitialBalance(new BigDecimal("1000.00"));

        AccountCreateRequest destination = new AccountCreateRequest();
        destination.setName("Destination");
        destination.setInitialBalance(new BigDecimal("0.00"));

        fromId = accountService.create(origin).getId();
        toId   = accountService.create(destination).getId();
    }

    /**
     * Cenário principal: 10 threads concorrentes tentam transferir R$ 100 ao mesmo tempo.
     *
     * <p>O Optimistic Locking garante que somente as transações que leram a versão
     * mais recente da linha conseguem fazer commit. As demais recebem
     * {@code ObjectOptimisticLockingFailureException} e são descartadas neste teste
     * (em produção o cliente recebe 409 Conflict e pode retentar).</p>
     */
    @Test
    void transferencias_concorrentes_devem_preservar_saldo_total() throws InterruptedException {
        int threads = 10;
        BigDecimal amount       = new BigDecimal("100.00");
        BigDecimal totalInicial = new BigDecimal("1000.00");

        // startLatch: mantém todas as threads bloqueadas até o disparo simultâneo
        CountDownLatch startLatch   = new CountDownLatch(1);
        // doneLatch: aguarda todas as threads finalizarem antes das asserções
        CountDownLatch doneLatch    = new CountDownLatch(threads);
        AtomicInteger  successCount = new AtomicInteger(0);

        ExecutorService executor = Executors.newFixedThreadPool(threads);

        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await(); // bloqueia até o disparo simultâneo
                    transferService.transfer(fromId, toId, amount);
                    successCount.incrementAndGet();
                } catch (Exception ignored) {
                    // ObjectOptimisticLockingFailureException é esperada sob contenção:
                    // indica que o Optimistic Locking detectou conflito de versão e
                    // abortou a transação para proteger a consistência dos dados.
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown(); // dispara todas as threads ao mesmo tempo
        doneLatch.await();      // aguarda conclusão de todas
        executor.shutdown();

        BigDecimal balanceFrom = accountRepository.findById(fromId).orElseThrow().getBalance();
        BigDecimal balanceTo   = accountRepository.findById(toId).orElseThrow().getBalance();

        // Invariante 1: conservação de valor — dinheiro não pode ser criado nem destruído
        assertThat(balanceFrom.add(balanceTo))
                .isEqualByComparingTo(totalInicial);

        // Invariante 2: pelo menos uma transferência deve ter sido confirmada
        assertThat(successCount.get())
                .isGreaterThan(0);

        // Invariante 3: nenhum saldo pode ser negativo (sem double-spend)
        assertThat(balanceFrom)
                .isGreaterThanOrEqualTo(BigDecimal.ZERO);
        assertThat(balanceTo)
                .isGreaterThanOrEqualTo(BigDecimal.ZERO);

        // Invariante 4: saldo do destino reflete exatamente as transferências confirmadas
        assertThat(balanceTo)
                .isEqualByComparingTo(amount.multiply(BigDecimal.valueOf(successCount.get())));
    }
}
