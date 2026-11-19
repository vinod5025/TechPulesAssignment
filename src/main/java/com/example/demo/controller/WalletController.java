
package com.example.demo.controller;

import java.math.BigDecimal;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import com.example.demo.dto.AddMoneyRequest;
import com.example.demo.dto.TransferRequest;
import com.example.demo.dto.TransactionResponse;
import com.example.demo.dto.WalletResponse;

import com.example.demo.service.WalletService;

@RestController
@RequestMapping("/wallet")
public class WalletController {

	private final WalletService walletService;

	public WalletController(WalletService walletService) {
		this.walletService = walletService;
	}

	// Add Money
	@PostMapping("/add")
	public TransactionResponse addMoney(@Valid @RequestBody AddMoneyRequest request,
			@RequestHeader("Idempotency-Key") String idempotencyKey, Authentication authentication) {

		String email = authentication.getName();
		BigDecimal amount = request.getAmount();

		return walletService.addMoney(email, amount, idempotencyKey);
	}

	// Check Balance
	@GetMapping
	public WalletResponse getBalance(Authentication authentication) {

		String email = authentication.getName();

		return walletService.getBalance(email);
	}

	// Transfer Money
	@PostMapping("/transfer")
	public TransactionResponse transferMoney(@Valid @RequestBody TransferRequest request,
			@RequestHeader("Idempotency-Key") String idempotencyKey, Authentication authentication) {

		String email = authentication.getName();

		return walletService.transferMoney(email, request, idempotencyKey);
	}
}
