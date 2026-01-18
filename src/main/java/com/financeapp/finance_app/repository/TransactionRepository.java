package com.financeapp.finance_app.repository;

import com.financeapp.finance_app.model.Catagory;
import com.financeapp.finance_app.model.Transaction;
import com.financeapp.finance_app.model.user;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUser(user user);
    Page<Transaction> findByUser(user user, Pageable pageable);
    Optional<Transaction> findByPlaidTransactionId(String plaidTransactionId);
    List<Transaction> findByUserAndCatagory(user user, Catagory category);

}
