package com.financeapp.finance_app.service;

import com.financeapp.finance_app.model.Catagory;
import com.financeapp.finance_app.model.Transaction;
import com.financeapp.finance_app.model.user;
import com.financeapp.finance_app.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }
    public Transaction saveTransaction(user user,String description, BigDecimal amount, Catagory category, LocalDateTime date) {
        Transaction transaction = new Transaction(description, amount, category, date);
        transaction.setUser(user);
        return transactionRepository.save(transaction);
    }
    public List<Transaction> getTransactionsByUser(user user){
        return transactionRepository.findByUser(user);
    }
}
