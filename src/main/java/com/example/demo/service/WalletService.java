
package com.example.demo.service;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.TransactionResponse;
import com.example.demo.dto.WalletResponse;
import com.example.demo.dto.TransferRequest;

import com.example.demo.entity.Transaction;
import com.example.demo.entity.TransactionStatus;
import com.example.demo.entity.TransactionType;
import com.example.demo.entity.Wallet;

import com.example.demo.repository.TransactionRepository;
import com.example.demo.repository.WalletRepository;

@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    public WalletService(
            WalletRepository walletRepository,
            TransactionRepository transactionRepository) {

        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    // Add Money
    @Transactional
    public TransactionResponse addMoney(
            String email,
            BigDecimal amount,
            String idempotencyKey) {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Amount must be greater than zero");
        }

        if (idempotencyKey == null || idempotencyKey.trim().isEmpty()) {
            throw new RuntimeException("Idempotency key is required");
        }

        Wallet wallet = walletRepository
                .findWalletByEmailForUpdate(email)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        Optional<Transaction> existingTransaction =
                transactionRepository.findByIdempotencyKey(idempotencyKey);

        if (existingTransaction.isPresent()) {

            Transaction transaction = existingTransaction.get();

            if (transaction.getToWallet() == null ||
                    !transaction.getToWallet().getUser().getEmail().equals(email)) {

                throw new RuntimeException("Idempotency key already used");
            }

            if (transaction.getType() != TransactionType.ADD ||
                    transaction.getAmount().compareTo(amount) != 0) {

                throw new RuntimeException(
                        "Idempotency key used with different request");
            }

            return convertToResponse(transaction);
        }

        wallet.setBalance(wallet.getBalance().add(amount));

        walletRepository.save(wallet);

        Transaction transaction = new Transaction(
                null,
                wallet,
                amount,
                TransactionType.ADD,
                TransactionStatus.SUCCESS,
                idempotencyKey
        );

        Transaction savedTransaction =
                transactionRepository.save(transaction);

        return convertToResponse(savedTransaction);
    }


    // Get Wallet Balance
    @Transactional(readOnly = true)
    public WalletResponse getBalance(String email) {

        Wallet wallet = walletRepository
                .findByUser_Email(email)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        return new WalletResponse(
                wallet.getId(),
                wallet.getUser().getEmail(),
                wallet.getBalance()
        );
    }


    // Transfer Money
    @Transactional
    public TransactionResponse transferMoney(
            String email,
            TransferRequest request,
            String idempotencyKey) {

        BigDecimal amount = request.getAmount();
        Long receiverWalletId = request.getReceiverWalletId();

        // Validate amount
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Amount must be greater than zero");
        }

        // Validate idempotency key
        if (idempotencyKey == null || idempotencyKey.trim().isEmpty()) {
            throw new RuntimeException("Idempotency key is required");
        }

        // Find sender wallet
        Wallet sender = walletRepository
                .findByUser_Email(email)
                .orElseThrow(() -> new RuntimeException("Sender wallet not found"));

        // Sender and receiver cannot be same
        if (sender.getId().equals(receiverWalletId)) {
            throw new RuntimeException("Cannot transfer money to your own wallet");
        }

        // Check duplicate transaction
        Optional<Transaction> existingTransaction =
                transactionRepository.findByIdempotencyKey(idempotencyKey);

        if (existingTransaction.isPresent()) {

            Transaction transaction = existingTransaction.get();

            if (transaction.getType() != TransactionType.TRANSFER ||
                    transaction.getFromWallet() == null ||
                    transaction.getToWallet() == null ||
                    !transaction.getFromWallet().getUser().getEmail().equals(email) ||
                    !transaction.getToWallet().getId().equals(receiverWalletId) ||
                    transaction.getAmount().compareTo(amount) != 0) {

                throw new RuntimeException(
                        "Idempotency key used with different request");
            }

            return convertToResponse(transaction);
        }

        // Check receiver exists
        Wallet receiver = walletRepository
                .findById(receiverWalletId)
                .orElseThrow(() -> new RuntimeException("Receiver wallet not found"));

        // Lock wallets in ID order to reduce deadlock risk
        Long firstWalletId = Math.min(sender.getId(), receiver.getId());
        Long secondWalletId = Math.max(sender.getId(), receiver.getId());

        Wallet firstWallet = walletRepository
                .findWalletByIdForUpdate(firstWalletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        Wallet secondWallet = walletRepository
                .findWalletByIdForUpdate(secondWalletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        // Identify sender and receiver after locking
        if (firstWallet.getId().equals(sender.getId())) {
            sender = firstWallet;
            receiver = secondWallet;
        } else {
            sender = secondWallet;
            receiver = firstWallet;
        }

        // Check sufficient balance
        if (sender.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        // Deduct amount from sender
        sender.setBalance(sender.getBalance().subtract(amount));

        // Add amount to receiver
        receiver.setBalance(receiver.getBalance().add(amount));

        walletRepository.save(sender);
        walletRepository.save(receiver);

        // Save transaction record
        Transaction transaction = new Transaction(
                sender,
                receiver,
                amount,
                TransactionType.TRANSFER,
                TransactionStatus.SUCCESS,
                idempotencyKey
        );

        Transaction savedTransaction =
                transactionRepository.save(transaction);

        return convertToResponse(savedTransaction);
    }


    // Convert Entity into Response DTO
    private TransactionResponse convertToResponse(Transaction transaction) {

        Long fromWalletId = transaction.getFromWallet() == null
                ? null
                : transaction.getFromWallet().getId();

        Long toWalletId = transaction.getToWallet() == null
                ? null
                : transaction.getToWallet().getId();

        return new TransactionResponse(
                transaction.getId(),
                transaction.getAmount(),
                transaction.getType(),
                transaction.getStatus(),
                fromWalletId,
                toWalletId,
                transaction.getCreatedAt()
        );
    }
}
