package com.ricaragas.paymybuddy.repository;

import com.ricaragas.paymybuddy.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

}
