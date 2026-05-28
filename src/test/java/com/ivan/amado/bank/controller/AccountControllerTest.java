package com.ivan.amado.bank.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ivan.amado.bank.dto.AccountCreateRequest;
import com.ivan.amado.bank.dto.AccountResponse;
import com.ivan.amado.bank.exception.AccountNotFoundException;
import com.ivan.amado.bank.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    AccountService accountService;

    @Test
    void deve_criar_conta_e_retornar_201() throws Exception {
        UUID id = UUID.randomUUID();
        AccountCreateRequest request = new AccountCreateRequest("Alice", new BigDecimal("500.00"));
        AccountResponse response = new AccountResponse(id, "Alice", new BigDecimal("500.00"));

        when(accountService.create(any(AccountCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Alice"))
                .andExpect(jsonPath("$.balance").value(500.00));
    }

    @Test
    void deve_retornar_conta_por_id() throws Exception {
        UUID id = UUID.randomUUID();
        AccountResponse response = new AccountResponse(id, "Bob", new BigDecimal("1000.00"));

        when(accountService.getById(id)).thenReturn(response);

        mockMvc.perform(get("/accounts/" + id).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Bob"));
    }

    @Test
    void deve_retornar_404_quando_conta_nao_encontrada() throws Exception {
        UUID id = UUID.randomUUID();

        when(accountService.getById(id)).thenThrow(new AccountNotFoundException(id));

        mockMvc.perform(get("/accounts/" + id).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }
}
