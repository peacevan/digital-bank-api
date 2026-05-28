package com.ivan.bank.infrastructure.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ivan.bank.application.dto.AccountResponse;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
public class AccountControllerGetTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GetAccountUseCase getAccountUseCase;
    
    @MockBean
    private com.ivan.bank.application.port.input.CreateAccountUseCase createAccountUseCase;

    @Test
    void getAccount_returns200() throws Exception {
        UUID id = UUID.randomUUID();
        AccountResponse resp = new AccountResponse(id, "John", new BigDecimal("20.00"));
        Mockito.when(getAccountUseCase.getById(id)).thenReturn(resp);

        mockMvc.perform(get("/accounts/" + id)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(resp)));
    }
}
