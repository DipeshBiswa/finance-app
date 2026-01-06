package com.financeapp.finance_app.repository;

import com.financeapp.finance_app.model.user;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<user, Long> {
    Optional<user> findByEmail(String email);
    Optional<user> findByUsername(String username);
    boolean existsByEmail(String email);
}

