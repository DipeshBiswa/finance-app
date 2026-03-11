package com.financeapp.finance_app.service.Tools;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.financeapp.finance_app.model.Catagory;
import com.financeapp.finance_app.model.Transaction;
import com.financeapp.finance_app.model.user;
import com.financeapp.finance_app.service.TransactionService;

import dev.langchain4j.agent.tool.Tool;

@Component
public class UserTransactionDataTool {
    TransactionService transactionService;

    public UserTransactionDataTool(TransactionService transactionService){
        this.transactionService = transactionService;
    }
    
    @Tool("gets all the transactions of the user")
    public String getUserTransactionData(user user){
        List<Transaction> userTransactions = transactionService.getTransactionsByUser(user);
        return "User Transactions: " + userTransactions.toString();
    }
    @Tool("gets the total spending of the user for the current month")
    public String getUserTransactionByCategory(user user){
        Map<Catagory, BigDecimal> transactionsByCategory = transactionService.filterByCatagory(user);
        return "User Transactions by Category: " + transactionsByCategory.toString();
    }

}
