package com.ivan.amado.bank.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class TransferRecord {

    private UUID id;
    private UUID fromAccountId;
    private UUID toAccountId;
    private BigDecimal amount;
    private Instant createdAt;
    private String direction;

    public TransferRecord() {
    }

    public TransferRecord(UUID id, UUID fromAccountId, UUID toAccountId, BigDecimal amount, Instant createdAt, String direction) {
        this.id = id;
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
        this.createdAt = createdAt;
        this.direction = direction;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getFromAccountId() { return fromAccountId; }
    public void setFromAccountId(UUID fromAccountId) { this.fromAccountId = fromAccountId; }

    public UUID getToAccountId() { return toAccountId; }
    public void setToAccountId(UUID toAccountId) { this.toAccountId = toAccountId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public String getDirection() { return direction; }
    public void setDirection(String direction) { this.direction = direction; }
}
