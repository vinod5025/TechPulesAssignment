package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

import com.example.demo.entity.Wallet;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

	Optional<Wallet> findByUser_Id(Long userId);

	Optional<Wallet> findByUser_Email(String email);

	boolean existsByUser_Id(Long userId);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT w FROM Wallet w WHERE w.user.email = :email")
	Optional<Wallet> findWalletByEmailForUpdate(@Param("email") String email);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT w FROM Wallet w WHERE w.id = :walletId")
	Optional<Wallet> findWalletByIdForUpdate(@Param("walletId") Long walletId);
}