package com.ivan.bank.application.port.input;

import com.ivan.bank.application.dto.AccountCreateRequest;
import com.ivan.bank.application.dto.AccountResponse;

public interface CreateAccountUseCase {
    AccountResponse create(AccountCreateRequest request);
}
