package com.ivan.amado.bank.application.port.input;

import java.math.BigDecimal;
import java.util.UUID;

public interface TransferUseCase {
    void transfer(UUID fromAccountId, UUID toAccountId, BigDecimal amount);
}
