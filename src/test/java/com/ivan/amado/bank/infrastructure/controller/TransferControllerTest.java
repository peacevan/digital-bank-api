package com.ivan.amado.bank.infrastructure.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ivan.amado.bank.application.dto.TransferRequest;
import com.ivan.amado.bank.application.port.input.TransferUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransferController.class)
public class TransferControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    TransferUseCase transferUseCase;

    @Test
    void should_return_ok_when_transfer_succeeds() throws Exception {
        TransferRequest req = new TransferRequest();
        req.setFromAccountId(UUID.randomUUID());
        req.setToAccountId(UUID.randomUUID());
        req.setAmount(new BigDecimal("10.00"));

        mockMvc.perform(post("/transfers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"));

        verify(transferUseCase).transfer(req.getFromAccountId(), req.getToAccountId(), req.getAmount());
    }

    @Test
    void should_return_bad_request_when_usecase_throws() throws Exception {
        TransferRequest req = new TransferRequest();
        req.setFromAccountId(UUID.randomUUID());
        req.setToAccountId(UUID.randomUUID());
        req.setAmount(new BigDecimal("1000.00"));

        doThrow(new IllegalArgumentException("Insufficient funds"))
                .when(transferUseCase).transfer(req.getFromAccountId(), req.getToAccountId(), req.getAmount());

        mockMvc.perform(post("/transfers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("ERROR"))
                .andExpect(jsonPath("$.message").value("Insufficient funds"));
    }
}
