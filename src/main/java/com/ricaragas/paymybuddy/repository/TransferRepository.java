package com.ricaragas.paymybuddy.repository;

import com.ricaragas.paymybuddy.model.Transfer;
import com.ricaragas.paymybuddy.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {

    List<Transfer>  findAllByConnection_creatorOrderByTimeCompletedDesc(Wallet wallet);
}
