package com.financeapp.finance_app.repository;

import com.financeapp.finance_app.model.Transaction;
import com.financeapp.finance_app.model.user;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUser(user user);


}
