package com.ivan.amado.bank.exception;

import java.math.BigDecimal;
import java.util.UUID;

public class InsufficientFundsException extends RuntimeException {

    public InsufficientFundsException(UUID accountId, BigDecimal balance, BigDecimal amount) {
        super("Insufficient funds on account " + accountId
                + ": balance=" + balance + ", requested=" + amount);
    }
}
