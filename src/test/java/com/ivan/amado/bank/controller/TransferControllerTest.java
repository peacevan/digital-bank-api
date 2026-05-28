package com.ivan.amado.bank.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ivan.amado.bank.dto.TransferRequest;
import com.ivan.amado.bank.exception.InsufficientFundsException;
import com.ivan.amado.bank.repository.IdempotencyRepository;
import com.ivan.amado.bank.repository.TransferRepository;
import com.ivan.amado.bank.service.TransferService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransferController.class)
class TransferControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    TransferService transferService;

    @MockBean
    TransferRepository transferRepository;

    @MockBean
    IdempotencyRepository idempotencyRepository;

    @Test
    void deve_retornar_ok_quando_transferencia_realizada() throws Exception {
        TransferRequest req = new TransferRequest();
        req.setFromAccountId(UUID.randomUUID());
        req.setToAccountId(UUID.randomUUID());
        req.setAmount(new BigDecimal("10.00"));

        mockMvc.perform(post("/transfers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"));

        verify(transferService).transfer(req.getFromAccountId(), req.getToAccountId(), req.getAmount());
    }

    @Test
    void deve_retornar_422_quando_saldo_insuficiente() throws Exception {
        UUID from = UUID.randomUUID();
        UUID to   = UUID.randomUUID();

        TransferRequest req = new TransferRequest();
        req.setFromAccountId(from);
        req.setToAccountId(to);
        req.setAmount(new BigDecimal("1000.00"));

        doThrow(new InsufficientFundsException(from, new BigDecimal("50.00"), new BigDecimal("1000.00")))
                .when(transferService).transfer(from, to, new BigDecimal("1000.00"));

        when(idempotencyRepository.findById(org.mockito.ArgumentMatchers.any())).thenReturn(java.util.Optional.empty());

        mockMvc.perform(post("/transfers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").exists());
    }
}
