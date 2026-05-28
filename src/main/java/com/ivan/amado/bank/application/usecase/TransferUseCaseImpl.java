package com.ivan.amado.bank.application.usecase;

import com.ivan.amado.bank.application.port.input.TransferUseCase;
import com.ivan.amado.bank.application.port.output.AccountRepository;
import com.ivan.amado.bank.application.port.output.Notifier;
import com.ivan.amado.bank.domain.model.Account;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import org.springframework.transaction.annotation.Transactional;
import java.util.Objects;
import java.util.UUID;

@Service
public class TransferUseCaseImpl implements TransferUseCase {

    private final AccountRepository accountRepository;
    private final Notifier notifier;

    @org.springframework.beans.factory.annotation.Autowired
    public TransferUseCaseImpl(AccountRepository accountRepository) {
        this(accountRepository, new NoOpNotifier());
    }

    public TransferUseCaseImpl(AccountRepository accountRepository, Notifier notifier) {
        this.accountRepository = accountRepository;
        this.notifier = notifier;
    }

    @Override
    @Transactional
    public void transfer(UUID fromAccountId, UUID toAccountId, BigDecimal amount) {
        Objects.requireNonNull(fromAccountId, "fromAccountId is required");
        Objects.requireNonNull(toAccountId, "toAccountId is required");
        Objects.requireNonNull(amount, "amount is required");

        if (fromAccountId.equals(toAccountId)) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        Account from = accountRepository.findById(fromAccountId)
                .orElseThrow(() -> new IllegalArgumentException("Source account not found"));
        Account to = accountRepository.findById(toAccountId)
                .orElseThrow(() -> new IllegalArgumentException("Destination account not found"));

        if (from.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient funds");
        }

        from.debit(amount);
        to.credit(amount);

        accountRepository.save(from);
        accountRepository.save(to);

        // send notifications
        notifier.notify(from.getId(), "Transfer sent: " + amount.toPlainString());
        notifier.notify(to.getId(), "Transfer received: " + amount.toPlainString());
    }

    private static class NoOpNotifier implements Notifier {
        @Override
        public void notify(UUID accountId, String message) {
            // no-op
        }
    }
}
