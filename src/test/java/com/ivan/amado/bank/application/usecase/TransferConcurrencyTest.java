package com.ivan.amado.bank.application.usecase;

import com.ivan.amado.bank.infrastructure.persistence.entity.AccountEntity;
import com.ivan.amado.bank.application.port.input.TransferUseCase;
import com.ivan.amado.bank.infrastructure.persistence.repository.SpringDataAccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class TransferConcurrencyTest {

    @Autowired
    private TransferUseCase transferUseCase;

    @Autowired
    private SpringDataAccountRepository accountRepository;

    @Test
    void concurrent_transfers_should_not_lose_money() throws Exception {
        UUID fromId = UUID.randomUUID();
        UUID toId = UUID.randomUUID();

        AccountEntity from = new AccountEntity(fromId, "A", new BigDecimal("100.00"));
        AccountEntity to = new AccountEntity(toId, "B", new BigDecimal("0.00"));
        accountRepository.save(from);
        accountRepository.save(to);

        ExecutorService ex = Executors.newFixedThreadPool(2);
        CountDownLatch startLatch = new CountDownLatch(1);
        AtomicInteger success = new AtomicInteger(0);
        AtomicInteger failure = new AtomicInteger(0);

        Runnable task = () -> {
            try {
                startLatch.await();
                transferUseCase.transfer(fromId, toId, new BigDecimal("60.00"));
                success.incrementAndGet();
            } catch (Throwable t) {
                failure.incrementAndGet();
            }
        };

        ex.submit(task);
        ex.submit(task);

        startLatch.countDown();
        ex.shutdown();
        ex.awaitTermination(10, TimeUnit.SECONDS);

        // reload balances
        BigDecimal fromBalance = accountRepository.findById(fromId).get().getBalance();
        BigDecimal toBalance = accountRepository.findById(toId).get().getBalance();

        // total must remain 100
        assertEquals(new BigDecimal("100.00"), fromBalance.add(toBalance));
        // at least one should have succeeded and at least one may have failed
        assertTrue(success.get() + failure.get() == 2);
        assertTrue(success.get() >= 1);
    }
}
