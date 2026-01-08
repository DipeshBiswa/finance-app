package com.financeapp.finance_app.controller;

import com.financeapp.finance_app.model.Transaction;
import com.financeapp.finance_app.model.user;
import com.financeapp.finance_app.service.TransactionService;
import com.financeapp.finance_app.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/transaction")
public class TransactionController {
    private final TransactionService transactionService;
    private final UserService userService;
    public TransactionController(TransactionService transactionService, UserService userService) {
        this.transactionService = transactionService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<?> postTransaction(@RequestBody Transaction transaction, Principal principal) {
        try {
            String username = principal.getName();
            user user = (user) userService.loadUserByUsername(username);
            Transaction transaction1 = transactionService.saveTransaction(user, transaction.getDescription(), transaction.getAmount(), transaction.getCatagory(), transaction.getDate());
            return new ResponseEntity<>(transaction1, HttpStatus.CREATED);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping
    public ResponseEntity<?> getTransactions(Principal principal) {
        try{
            String username = principal.getName();
            user user = (user) userService.loadUserByUsername(username);
            List<Transaction> userTransactions = transactionService.getTransactionsByUser(user);
            return new ResponseEntity<>(userTransactions, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
