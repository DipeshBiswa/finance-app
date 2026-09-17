package com.financeapp.finance_app.service.Tools;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.financeapp.finance_app.model.user;
import com.financeapp.finance_app.model.Catagory;
import com.financeapp.finance_app.service.GoalService;
import com.financeapp.finance_app.service.UserService;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolMemoryId;

@Component
public class UserGoalDataTool {

    @Autowired
    private GoalService goalService;
    @Autowired
    private UserService userService;

    public UserGoalDataTool(GoalService goalService, UserService userService){
        this.goalService = goalService;
        this.userService = userService;
    }

    @Tool("Gets all of the goals of the user")
    public List<GoalSummary> getUserGoals(@ToolMemoryId Long userId){
        user user = userService.findById(userId).orElseThrow();
        return goalService.getGoalsByUser(user).stream()
                .map(goal -> new GoalSummary(goal.getTitle(), goal.getDescription(),
                        goal.getCurrent_amount(), goal.getTarget_amount(), goal.getCategory()))
                .toList();
    }

    public record GoalSummary(String title, String description, BigDecimal currentAmount,
                              BigDecimal targetAmount, Catagory category) {}
}
