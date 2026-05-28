package com.ivan.amado.bank.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class AccountResponse {

    private UUID id;
    private String name;
    private BigDecimal balance;

    public AccountResponse() {
    }

    public AccountResponse(UUID id, String name, BigDecimal balance) {
        this.id = id;
        this.name = name;
        this.balance = balance;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
}
