package com.financeapp.finance_app.service;

import com.financeapp.finance_app.model.Catagory;
import com.financeapp.finance_app.model.Goal;
import com.financeapp.finance_app.model.Transaction;
import com.financeapp.finance_app.model.user;
import com.financeapp.finance_app.repository.GoalRepository;
import com.financeapp.finance_app.repository.TransactionRepository;
import com.financeapp.finance_app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class GoalService {
    @Autowired
    private final GoalRepository goalRepository;
    @Autowired
    private final TransactionRepository transactionRepository;
    public GoalService(GoalRepository goalRepository, TransactionRepository transactionRepository) {
        this.goalRepository = goalRepository;
        this.transactionRepository = transactionRepository;
    }
    public Goal createGoal(user user, String title, String description, BigDecimal target_amount, LocalDateTime date, Catagory category) {
        Goal goal = new Goal(title, description, target_amount, date, category);
        goal.setUser(user);
        return goalRepository.save(goal);
    }
    public void updateGoalProgress(user user){
        List<Goal> userGoals = goalRepository.findAllByUser(user);
        for(Goal goal: userGoals){
            List<Transaction> transactions= transactionRepository.findByUserAndCategory(user, goal.getCategory());
            BigDecimal total = BigDecimal.ZERO;
            for(Transaction transaction: transactions){
                total = total.add(transaction.getAmount());
            }
            goal.setCurrent_amount(total);
            goalRepository.save(goal);
        }

    }


}
