package com.ivan.amado.bank.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ivan.amado.bank.dto.AccountCreateRequest;
import com.ivan.amado.bank.dto.AccountResponse;
import com.ivan.amado.bank.dto.TransferRequest;
import com.ivan.amado.bank.repository.AccountRepository;
import com.ivan.amado.bank.repository.IdempotencyRepository;
import com.ivan.amado.bank.repository.TransferRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EndpointIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    TransferRepository transferRepository;

    @Autowired
    IdempotencyRepository idempotencyRepository;

    @BeforeEach
    void limparBancoDeDados() {
        idempotencyRepository.deleteAll();
        transferRepository.deleteAll();
        accountRepository.deleteAll();
    }

    private UUID createAccount(String name, BigDecimal balance) throws Exception {
        AccountCreateRequest req = new AccountCreateRequest(name, balance);
        MvcResult result = mockMvc.perform(post("/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value(name))
                .andReturn();
        AccountResponse resp = objectMapper.readValue(result.getResponse().getContentAsString(), AccountResponse.class);
        return resp.getId();
    }

    @Test
    void deve_criar_conta_e_consultar_por_id() throws Exception {
        UUID id = createAccount("Alice", new BigDecimal("500.00"));

        mockMvc.perform(get("/accounts/" + id).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Alice"))
                .andExpect(jsonPath("$.balance").value(500.00));
    }

    @Test
    void deve_realizar_transferencia_entre_contas() throws Exception {
        UUID from = createAccount("Bob", new BigDecimal("1000.00"));
        UUID to   = createAccount("Carol", new BigDecimal("0.00"));

        TransferRequest req = new TransferRequest();
        req.setFromAccountId(from);
        req.setToAccountId(to);
        req.setAmount(new BigDecimal("300.00"));

        mockMvc.perform(post("/transfers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"));

        mockMvc.perform(get("/accounts/" + from).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(700.00));

        mockMvc.perform(get("/accounts/" + to).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(300.00));
    }

    @Test
    void deve_retornar_erro_quando_saldo_insuficiente() throws Exception {
        UUID from = createAccount("Dave", new BigDecimal("50.00"));
        UUID to   = createAccount("Eve", new BigDecimal("0.00"));

        TransferRequest req = new TransferRequest();
        req.setFromAccountId(from);
        req.setToAccountId(to);
        req.setAmount(new BigDecimal("200.00"));

        mockMvc.perform(post("/transfers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void deve_retornar_historico_de_transferencias() throws Exception {
        UUID from = createAccount("Frank", new BigDecimal("1000.00"));
        UUID to   = createAccount("Grace", new BigDecimal("0.00"));

        TransferRequest req = new TransferRequest();
        req.setFromAccountId(from);
        req.setToAccountId(to);
        req.setAmount(new BigDecimal("100.00"));

        mockMvc.perform(post("/transfers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/transfers/account/" + from).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].fromAccountId").value(from.toString()))
                .andExpect(jsonPath("$.content[0].amount").value(100.00));
    }

    @Test
    void deve_processar_idempotencia_na_transferencia() throws Exception {
        UUID from = createAccount("Henry", new BigDecimal("1000.00"));
        UUID to   = createAccount("Iris", new BigDecimal("0.00"));

        TransferRequest req = new TransferRequest();
        req.setFromAccountId(from);
        req.setToAccountId(to);
        req.setAmount(new BigDecimal("100.00"));

        String idempotencyKey = UUID.randomUUID().toString();

        mockMvc.perform(post("/transfers")
                .header("Idempotency-Key", idempotencyKey)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"));

        mockMvc.perform(post("/transfers")
                .header("Idempotency-Key", idempotencyKey)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"));

        mockMvc.perform(get("/accounts/" + from).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(900.00));
    }

    @Test
    void deve_processar_idempotencia_via_query_param() throws Exception {
        UUID from = createAccount("Jack", new BigDecimal("1000.00"));
        UUID to   = createAccount("Kara", new BigDecimal("0.00"));

        TransferRequest req = new TransferRequest();
        req.setFromAccountId(from);
        req.setToAccountId(to);
        req.setAmount(new BigDecimal("100.00"));

        String idempotencyKey = UUID.randomUUID().toString();

        mockMvc.perform(post("/transfers?Idempotency-Key=" + idempotencyKey)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"));

        // second call with same query param must be idempotent
        mockMvc.perform(post("/transfers?Idempotency-Key=" + idempotencyKey)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"));

        // balance should be debited only once
        mockMvc.perform(get("/accounts/" + from).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(900.00));

        // repository should contain the idempotency key saved
        assertTrue(idempotencyRepository.findById(idempotencyKey).isPresent(), "Idempotency key should be persisted");
    }
}
