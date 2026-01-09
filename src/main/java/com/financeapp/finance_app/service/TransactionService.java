package com.financeapp.finance_app.service;

import com.financeapp.finance_app.model.Catagory;
import com.financeapp.finance_app.model.Transaction;
import com.financeapp.finance_app.model.user;
import com.financeapp.finance_app.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public Map<Catagory, BigDecimal> filterByCatagory(user user){
        HashMap<Catagory, BigDecimal> map = new HashMap<>();
        List<Transaction> transactions = transactionRepository.findByUser(user);
        for(Transaction transaction: transactions){
            if(!map.containsKey(transaction.getCatagory())){
                map.put(transaction.getCatagory(), transaction.getAmount());
            }else{
                BigDecimal amount = map.get(transaction.getCatagory());
                map.put(transaction.getCatagory(), amount.add(transaction.getAmount()));
            }
        }
        return map;
    }
    public BigDecimal totalSpendingForCurrentMonth(user user){
        BigDecimal total = BigDecimal.ZERO;
        LocalDateTime currentDate = LocalDateTime.now();
        int year = currentDate.getYear();
        int month = currentDate.getMonthValue();
        List<Transaction> transactions = transactionRepository.findByUser(user);
        for(Transaction transaction: transactions){
            int transactionMonth =  transaction.getDate().getMonthValue();
            int transactionYear =  transaction.getDate().getYear();
            if(transactionMonth == month && transactionYear == year){
                total = total.add(transaction.getAmount());
            }
        }
        return total;
    }
    public Map<Integer, BigDecimal> getTotalSpendingForCertainYear(user user, int year){
        HashMap<Integer, BigDecimal> map = new HashMap<>();
        for(int i = 1; i<= 12;i++){
            map.put(i, new BigDecimal("0"));
        }
        List<Transaction> transactions = transactionRepository.findByUser(user);
        for(Transaction transaction: transactions){
            int transactionYear =  transaction.getDate().getYear();
            int transactionMonth =  transaction.getDate().getMonthValue();
            if(transactionYear == year){
                BigDecimal amount = map.get(transactionMonth);
                map.put(transactionMonth, amount.add(transaction.getAmount()));
            }
        }
        return map;
    }
}

