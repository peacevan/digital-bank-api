package com.ivan.bank.infrastructure.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ivan.bank.application.dto.AccountCreateRequest;
import com.ivan.bank.application.dto.AccountResponse;
import com.ivan.bank.application.port.input.CreateAccountUseCase;
import com.ivan.bank.application.port.input.GetAccountUseCase;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CreateAccountUseCase createAccountUseCase;

    @MockBean
    private GetAccountUseCase getAccountUseCase;

    @Test
    void postAccount_validRequest_returns201() throws Exception {
        AccountCreateRequest req = new AccountCreateRequest("Eve", new BigDecimal("50"));
        AccountResponse resp = new AccountResponse(UUID.randomUUID(), "Eve", new BigDecimal("50"));
        Mockito.when(createAccountUseCase.create(Mockito.any())).thenReturn(resp);

        mockMvc.perform(post("/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(resp)));
    }
}
