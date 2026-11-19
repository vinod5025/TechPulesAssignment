package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.demo.entity.TransactionStatus;
import com.example.demo.entity.TransactionType;

public class TransactionResponse {

    private Long transactionId;
    private BigDecimal amount;
    private TransactionType type;
    private TransactionStatus status;
    private Long fromWalletId;
    private Long toWalletId;
    private LocalDateTime createdAt;

    public TransactionResponse() {
    }

    public TransactionResponse(Long transactionId, BigDecimal amount,
            TransactionType type, TransactionStatus status,
            Long fromWalletId, Long toWalletId,
            LocalDateTime createdAt) {

        this.transactionId = transactionId;
        this.amount = amount;
        this.type = type;
        this.status = status;
        this.fromWalletId = fromWalletId;
        this.toWalletId = toWalletId;
        this.createdAt = createdAt;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public TransactionType getType() {
        return type;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public Long getFromWalletId() {
        return fromWalletId;
    }

    public Long getToWalletId() {
        return toWalletId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}