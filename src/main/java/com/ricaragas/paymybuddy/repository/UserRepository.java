package com.ricaragas.paymybuddy.repository;

import com.ricaragas.paymybuddy.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

}
