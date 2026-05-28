package com.ivan.amado.bank.application.port.input;

import com.ivan.amado.bank.application.dto.AccountCreateRequest;
import com.ivan.amado.bank.application.dto.AccountResponse;

public interface CreateAccountUseCase {
    AccountResponse create(AccountCreateRequest request);
}
