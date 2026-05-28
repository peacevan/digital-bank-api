package com.ivan.bank.domain.model;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class Account {
    private final UUID id;
    private final String name;
    private BigDecimal balance;
    private long version;

    public Account(UUID id, String name, BigDecimal balance) {
        this.id = id;
        this.name = name;
        this.balance = balance;
        this.version = 0L;
    }

    public static Account of(String name, BigDecimal balance) {
        return new Account(UUID.randomUUID(), name, balance);
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public long getVersion() {
        return version;
    }

    public void debit(java.math.BigDecimal amount) {
        this.balance = this.balance.subtract(amount);
    }

    public void credit(java.math.BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(id, account.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
