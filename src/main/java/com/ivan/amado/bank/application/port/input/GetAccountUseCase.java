package com.ivan.amado.bank.application.port.input;

import com.ivan.amado.bank.application.dto.AccountResponse;

import java.util.UUID;

public interface GetAccountUseCase {
    AccountResponse getById(UUID id);
}
