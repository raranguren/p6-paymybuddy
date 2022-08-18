package com.ricaragas.paymybuddy.repository;

import com.ricaragas.paymybuddy.model.Connection;
import com.ricaragas.paymybuddy.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConnectionRepository extends JpaRepository<Connection, Long> {

    Optional<Connection> findByCreatorAndTarget(Wallet creator, Wallet target);
}
