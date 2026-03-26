package com.financeapp.finance_app.service.Tools;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.financeapp.finance_app.model.Catagory;
import com.financeapp.finance_app.model.Transaction;
import com.financeapp.finance_app.model.user;
import com.financeapp.finance_app.service.TransactionService;
import com.financeapp.finance_app.service.UserService;

import dev.langchain4j.agent.tool.Tool;

@Component
public class UserTransactionDataTool {
    private TransactionService transactionService;
    private UserService userService;

    public UserTransactionDataTool(TransactionService transactionService, UserService userService){
        this.transactionService = transactionService;
        this.userService = userService;

    }
    
    @Tool("gets all the transactions of the user")
    public List<Transaction> getUserTransactionData(Long userid){
        user user = userService.findById(userid).orElseThrow();
        return transactionService.getTransactionsByUser(user);
        
    }
    @Tool("gets the total spending of the user for the current month")
    public Map<Catagory, BigDecimal> getUserTransactionByCategory(Long userid){
        user user = userService.findById(userid).orElseThrow();
        return transactionService.filterByCatagory(user);
    }
    @Tool("Get the account balance of the user")
    public BigDecimal getUserAccountBalance(Long userId){
        user user = userService.findById(userId).orElseThrow();
        return transactionService.getUserAccountBalance(user);
    }
}
