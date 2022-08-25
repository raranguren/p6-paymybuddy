package com.ricaragas.paymybuddy.repository;

import com.ricaragas.paymybuddy.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

    Optional<Wallet> findByUser_email(String email);
}
