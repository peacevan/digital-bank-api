package com.ivan.bank.application.port.input;

import com.ivan.bank.application.dto.AccountResponse;

import java.util.UUID;

public interface GetAccountUseCase {
    AccountResponse getById(UUID id);
}
