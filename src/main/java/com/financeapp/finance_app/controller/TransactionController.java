package com.financeapp.finance_app.controller;

import com.financeapp.finance_app.model.Catagory;
import com.financeapp.finance_app.model.Transaction;
import com.financeapp.finance_app.model.user;
import com.financeapp.finance_app.service.TransactionService;
import com.financeapp.finance_app.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Map;

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
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<?> getTransactions(Principal principal) {
        try {
            String username = principal.getName();
            user user = (user) userService.loadUserByUsername(username);
            List<Transaction> userTransactions = transactionService.getTransactionsByUser(user);
            return new ResponseEntity<>(userTransactions, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/summary/category")
    public ResponseEntity<?> getSummaryCategory(Principal principal) {
        try {
            String username = principal.getName();
            user user = (user) userService.loadUserByUsername(username);
            Map<Catagory, BigDecimal> userTransactions = transactionService.filterByCatagory(user);
            return new ResponseEntity<>(userTransactions, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/summary/month")
    public ResponseEntity<?> getSummaryMonth(Principal principal) {
        try{
            String username = principal.getName();
            user user = (user) userService.loadUserByUsername(username);
            BigDecimal amount = transactionService.totalSpendingForCurrentMonth(user);
            return new ResponseEntity<>(amount, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/summary")
    public ResponseEntity<?> getYearlySummary(Principal principal, @RequestParam Integer year){
        try{
            String username = principal.getName();
            user user = (user) userService.loadUserByUsername(username);
            Map<Integer, BigDecimal> yearly = transactionService.getTotalSpendingForCertainYear(user,year);
            return new ResponseEntity<>(yearly, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
